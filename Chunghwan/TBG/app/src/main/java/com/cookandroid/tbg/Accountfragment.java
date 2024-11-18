package com.cookandroid.tbg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Accountfragment extends Fragment {

    private ImageView profileImageView;
    private TextView accountNickname;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<Intent> selectImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        profileImageView.setImageURI(selectedImageUri);
                        uploadImageToServer(selectedImageUri);  // 이미지 업로드 메서드 호출
                    } else {
                        Toast.makeText(getContext(), "이미지를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accountfragment, container, false);




        profileImageView = view.findViewById(R.id.profileImageView);
        accountNickname = view.findViewById(R.id.accountNickname);
        profileImageView.setOnClickListener(v -> openGallery());
////////////////////////////////////////////////////////////////////
        // 닉네임 불러오기
        loadNickname();
        loadAccountStatistics();  // ★ 추가된 부분 ★
/////////////////////////////////////////////////////////////////////
        // LinearLayout 클릭 시 새로운 Activity로 이동하는 Intent 설정
        LinearLayout pwResetLayout = view.findViewById(R.id.pwReset);
        pwResetLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PWresetActivity.class);
            startActivity(intent);
        });

        // LinearLayout 클릭 시 새로운 Activity로 이동하는 Intent 설정
        LinearLayout settingLayout = view.findViewById(R.id.setting);
        settingLayout.setOnClickListener(v -> {  // 여기를 settingLayout으로 수정
            Intent intent = new Intent(getActivity(), SettingActivity.class);
            startActivity(intent);
        });

        // LinearLayout 클릭 시 새로운 Activity로 이동하는 Intent 설정
        LinearLayout suportLayout = view.findViewById(R.id.support);
        suportLayout.setOnClickListener(v -> {  // 여기를 SupportActivity로 수정
            Intent intent = new Intent(getActivity(), SupportActivity.class);
            startActivity(intent);
        });


        // 앱 시작 시 프로필 이미지 로드
        loadProfileImage();  // ★ 수정된 부분: 앱을 시작할 때 서버에서 프로필 이미지를 불러옵니다.

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Fragment가 다시 활성화될 때 통계 데이터 갱신
        loadAccountStatistics();
    }



    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // ACTION_PICK 대신 ACTION_GET_CONTENT로 시도
        intent.setType("image/*");
        selectImageLauncher.launch(intent);
    }

//닉네임 뿌려주기 ( HomeActivity 코드랑 똑같음)
    private void loadNickname() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
        String sessionId = prefs.getString("sessionId", null);

        if (sessionId != null) {
            new Thread(() -> {
                String serverUrl = "http://10.0.2.2:8888/kch_server/MainServlet";

                try {
                    URL url = new URL(serverUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    conn.setDoOutput(true);

                    String postData = "sessionId=" + sessionId;
                    OutputStream os = conn.getOutputStream();
                    os.write(postData.getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if ("success".equals(jsonResponse.getString("status"))) {
                            String nickname = jsonResponse.getString("nickname");
                            Log.d("NicknameUpdate", "Setting nickname to: " + nickname);
                            requireActivity().runOnUiThread(() -> accountNickname.setText( nickname + " 님"));
                        } else {
                            String message = jsonResponse.getString("message");
                            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
                        }
                    } else {
                        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Server error: " + responseCode, Toast.LENGTH_LONG).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error during request.", Toast.LENGTH_LONG).show());
                }
            }).start();
        } else {
            Toast.makeText(getContext(), "Session ID not found. Please log in.", Toast.LENGTH_SHORT).show();
        }
    }


    // 서버로 프로필 이미지를 업로드하는 메서드
    private void uploadImageToServer(Uri imageUri) {
        executor.execute(() -> {

            try {
                InputStream imageStream = requireActivity().getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                // ★ 이미지 회전이 필요한 경우 회전된 이미지를 반환하는 메서드를 호출합니다.
                selectedImage = rotateImageIfRequired(selectedImage, imageUri);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

                String serverUrl = "http://10.0.2.2:8888/kch_server/UploadProfileImage";
                URL url = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
                String sessionId = prefs.getString("sessionId", null);

                if (sessionId == null) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "세션 ID를 찾을 수 없습니다. 다시 로그인하세요.", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("sessionId", sessionId);
                jsonRequest.put("image", encodedImage);

                OutputStream os = conn.getOutputStream();
                os.write(jsonRequest.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    boolean success = "success".equals(jsonResponse.getString("status"));
                    requireActivity().runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(getContext(), "이미지가 서버에 성공적으로 업로드되었습니다.", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(this::loadProfileImage, 10000); // 10초 지연 후 프로필 이미지 로드(db에서 동기화인데 어차피 보여주는건 똑같으니까 매우 빠르게 프사 바꾸는경우에 유리)
                        } else {
                            Toast.makeText(getContext(), "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "이미지 업로드 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    // ★ 이미지 회전 보정을 위해 EXIF 정보를 확인하고 필요한 경우 이미지를 회전시키는 메서드.
    private Bitmap rotateImageIfRequired(Bitmap img, Uri imageUri) throws IOException {
        InputStream input = requireActivity().getContentResolver().openInputStream(imageUri);
        ExifInterface ei = new ExifInterface(input);

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);  // ★ 90도 회전 필요 시 회전.
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180); // ★ 180도 회전 필요 시 회전.
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270); // ★ 270도 회전 필요 시 회전.
            default:
                return img;  // ★ 회전 필요 없는 경우 원본 이미지 반환.
        }
    }

    // ★ 실제 이미지 회전을 수행하는 메서드.
    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);  // ★ 주어진 각도로 이미지를 회전합니다.
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }

    private void loadProfileImage() {
        executor.execute(() -> {
            try {
                SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
                String sessionId = prefs.getString("sessionId", null);
                if (sessionId == null) return;

                // 타임스탬프 추가하여 URL 생성
                String serverUrl = "http://10.0.2.2:8888/kch_server/GetProfileImage?sessionId=" + sessionId + "&timestamp=" + System.currentTimeMillis();
                URL url = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if ("success".equals(jsonResponse.getString("status"))) {
                        String imageUrl = jsonResponse.getString("imageUrl").replace("localhost", "10.0.2.2");
                        String imageUrlWithTimestamp = imageUrl + "?timestamp=" + System.currentTimeMillis(); // 타임스탬프 추가
                        System.out.println("Image URL received: " + imageUrlWithTimestamp);  // 디버그 로그 추가
                        requireActivity().runOnUiThread(() -> loadImageFromUrl(imageUrlWithTimestamp));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void loadImageFromUrl(String imageUrl) {
        executor.execute(() -> {
            try {
                System.out.println("Loading image from URL: " + imageUrl);  // 디버그 로그 추가
                InputStream input = new java.net.URL(imageUrl).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                requireActivity().runOnUiThread(() -> {
                    profileImageView.setImageBitmap(null); // 이전 이미지 지우기
                    profileImageView.setImageBitmap(bitmap); // 새 이미지 설정
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void loadAccountStatistics() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String sessionId = prefs.getString("sessionId", null);

        if (sessionId != null) {
            new Thread(() -> {
                try {
                    URL url = new URL("http://10.0.2.2:8888/kch_server/GetAccountStatistics?sessionId=" + sessionId);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        int favoriteCount = jsonResponse.getInt("favoriteCount");
                        int postCount = jsonResponse.getInt("postCount");

                        requireActivity().runOnUiThread(() -> {
                            // UI 업데이트
                            TextView favCountTextView = getView().findViewById(R.id.favCount);
                            TextView postCountTextView = getView().findViewById(R.id.postCount);

                            favCountTextView.setText(favoriteCount + " 회");
                            postCountTextView.setText(postCount + " 회");
                        });
                    } else {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "통계 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "서버 요청 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();
        } else {
            Toast.makeText(getContext(), "Session ID를 찾을 수 없습니다. 다시 로그인하세요.", Toast.LENGTH_SHORT).show();
        }
    }





}

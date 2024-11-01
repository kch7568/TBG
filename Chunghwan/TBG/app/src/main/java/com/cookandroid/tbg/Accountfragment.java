package com.cookandroid.tbg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Accountfragment extends Fragment {

    private ImageView profileImageView;
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
        profileImageView.setOnClickListener(v -> openGallery());

        // 앱 시작 시 프로필 이미지 로드
        loadProfileImage();  // ★ 수정된 부분: 앱을 시작할 때 서버에서 프로필 이미지를 불러옵니다.

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageLauncher.launch(intent);
    }

    // 서버로 프로필 이미지를 업로드하는 메서드
    private void uploadImageToServer(Uri imageUri) {
        executor.execute(() -> {

            try {
                InputStream imageStream = requireActivity().getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

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
                            loadProfileImage(); // ★ 업로드 후 프로필 이미지를 다시 로드하여 최신 상태로 갱신
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



    // 수정된 부분: 이미지를 불러오는 loadProfileImage 메서드에서 imageUrlWithTimestamp 설정 부분

    private void loadProfileImage() {
        executor.execute(() -> {
            try {
                SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
                String sessionId = prefs.getString("sessionId", null);
                if (sessionId == null) return;

                String serverUrl = "http://10.0.2.2:8888/kch_server/GetProfileImage?sessionId=" + sessionId;
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
                    System.out.println("Response from server: " + jsonResponse.toString());  // 디버그 로그 추가
                    if ("success".equals(jsonResponse.getString("status"))) {
                        String imageUrl = jsonResponse.getString("imageUrl").replace("localhost", "10.0.2.2"); // ★ localhost를 10.0.2.2로 변경
                        String imageUrlWithTimestamp = imageUrl + "?timestamp=" + System.currentTimeMillis();
                        System.out.println("Image URL received: " + imageUrlWithTimestamp);  // 디버그 로그 추가
                        requireActivity().runOnUiThread(() -> loadImageFromUrl(imageUrlWithTimestamp));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



    // ★ 추가된 메서드: URL에서 이미지를 로드하여 ImageView에 설정하는 메서드
    private void loadImageFromUrl(String imageUrl) {
        executor.execute(() -> {
            try {
                System.out.println("Loading image from URL: " + imageUrl);  // 디버그 로그 추가
                InputStream input = new java.net.URL(imageUrl).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                requireActivity().runOnUiThread(() -> profileImageView.setImageBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

package com.cookandroid.tbg;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class WritingActivity extends AppCompatActivity {

    private static final String TAG = "WritingActivity";
    private Uri selectedMediaUri;
    private ImageView selectedImageView;
    private VideoView selectedVideoView;
    private static final String SERVER_URL = "http://10.0.2.2:8888/kch_server/CreatePost";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noticeboard_writing);

        ImageButton cancelButton = findViewById(R.id.Post_cancle);
        cancelButton.setOnClickListener(v -> finish());

        ImageButton registerButton = findViewById(R.id.Post_Register);
        ImageButton uploadButton = findViewById(R.id.uploadButton);
        EditText titleInput = findViewById(R.id.titleInput);
        EditText contentInput = findViewById(R.id.contentInput);
        RadioGroup categoryGroup = findViewById(R.id.categoryGroup);
        selectedImageView = findViewById(R.id.selectedImageView);
        selectedVideoView = findViewById(R.id.selectedVideoView);

        selectedImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        selectedImageView.setAdjustViewBounds(true);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(selectedVideoView);
        selectedVideoView.setMediaController(mediaController);

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String sessionId = sharedPreferences.getString("sessionId", null);

        // 카테고리 매핑
        Map<Integer, String> categoryMap = new HashMap<>();
        categoryMap.put(R.id.category1, "A"); // 관광명소
        categoryMap.put(R.id.category2, "B"); // 교통수단
        categoryMap.put(R.id.category3, "C"); // 호텔
        categoryMap.put(R.id.category4, "D"); // 자유게시판

        ActivityResultLauncher<Intent> mediaPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedMediaUri = result.getData().getData();
                        if (selectedMediaUri != null) {
                            String mimeType = getContentResolver().getType(selectedMediaUri);
                            Log.d(TAG, "Selected media type: " + mimeType);
                            if (mimeType != null && mimeType.startsWith("image")) {
                                selectedImageView.setImageURI(selectedMediaUri);
                                selectedImageView.setVisibility(View.VISIBLE);
                                selectedVideoView.setVisibility(View.GONE);
                            } else if (mimeType != null && mimeType.startsWith("video")) {
                                selectedVideoView.setVisibility(View.VISIBLE);
                                selectedImageView.setVisibility(View.GONE);
                                selectedVideoView.setVideoURI(selectedMediaUri);
                                selectedVideoView.requestFocus();
                                selectedVideoView.start();
                            }
                        }
                    }
                }
        );

        uploadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mediaPickerLauncher.launch(intent);
        });

        selectedImageView.setOnClickListener(v -> {
            if (selectedMediaUri != null) {
                Dialog dialog = new Dialog(WritingActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_image_preview);
                ImageView imageView = dialog.findViewById(R.id.dialogImageView);
                if (imageView != null) {
                    imageView.setImageURI(selectedMediaUri);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.gravity = Gravity.CENTER;
                    dialog.getWindow().setAttributes(lp);
                    dialog.show();
                    imageView.setOnClickListener(view -> dialog.dismiss());
                }
            }
        });

        registerButton.setOnClickListener(v -> {
            if (sessionId == null) {
                Toast.makeText(this, "세션 ID를 찾을 수 없습니다. 다시 로그인하세요.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Session ID is missing");
                return;
            }

            String title = titleInput.getText().toString().trim();
            String content = contentInput.getText().toString().trim();
            int selectedCategoryId = categoryGroup.getCheckedRadioButtonId();
            String selectedCategory = categoryMap.get(selectedCategoryId);

            if (title.isEmpty() || content.isEmpty() || selectedCategory == null) {
                Toast.makeText(this, "모든 필드를 작성해주세요.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Title, content, or category is empty");
                return;
            }

            new Thread(() -> {
                try {
                    String boundary = "----WebKitFormBoundary" + UUID.randomUUID().toString();
                    URL url = new URL(SERVER_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    conn.setDoOutput(true);

                    try (OutputStream os = new BufferedOutputStream(conn.getOutputStream())) {
                        String lineEnd = "\r\n";
                        String twoHyphens = "--";

                        os.write((twoHyphens + boundary + lineEnd).getBytes());
                        os.write(("Content-Disposition: form-data; name=\"sessionId\"" + lineEnd + lineEnd + sessionId + lineEnd).getBytes());
                        os.write((twoHyphens + boundary + lineEnd).getBytes());
                        os.write(("Content-Disposition: form-data; name=\"title\"" + lineEnd + lineEnd + title + lineEnd).getBytes());
                        os.write((twoHyphens + boundary + lineEnd).getBytes());
                        os.write(("Content-Disposition: form-data; name=\"content\"" + lineEnd + lineEnd + content + lineEnd).getBytes());
                        os.write((twoHyphens + boundary + lineEnd).getBytes());
                        os.write(("Content-Disposition: form-data; name=\"category\"" + lineEnd + lineEnd + selectedCategory + lineEnd).getBytes());

                        if (selectedMediaUri != null) {
                            try (InputStream inputStream = getContentResolver().openInputStream(selectedMediaUri)) {
                                os.write((twoHyphens + boundary + lineEnd).getBytes());

                                String mimeType = getContentResolver().getType(selectedMediaUri);
                                String fileName = selectedMediaUri.getLastPathSegment() != null ? selectedMediaUri.getLastPathSegment().replaceAll("[:]", "_") : "uploaded_file";
                                String fileExtension = mimeType != null && mimeType.contains("/") ? mimeType.substring(mimeType.indexOf("/") + 1) : "jpg";
                                fileName += "." + fileExtension;

                                os.write(("Content-Disposition: form-data; name=\"media\"; filename=\"" + fileName + "\"" + lineEnd).getBytes());
                                os.write(("Content-Type: " + mimeType + lineEnd + lineEnd).getBytes());

                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    os.write(buffer, 0, bytesRead);
                                }
                                os.write(lineEnd.getBytes());
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading media file", e);
                            }
                        }

                        os.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes());
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }

                            try {
                                JSONObject jsonResponse = new JSONObject(response.toString());
                                runOnUiThread(() -> {
                                    Toast.makeText(this, jsonResponse.optString("message", "글이 성공적으로 등록되었습니다."), Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                            } catch (org.json.JSONException e) {
                                Log.e(TAG, "JSON parsing error: " + response.toString());
                                runOnUiThread(() -> Toast.makeText(this, "서버 오류: " + responseCode, Toast.LENGTH_LONG).show());
                            }
                        }
                    } else {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            Log.e(TAG, "Server error response: " + response.toString());
                            runOnUiThread(() -> Toast.makeText(this, "서버 오류: " + responseCode, Toast.LENGTH_LONG).show());
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error occurred while sending post data", e);
                    runOnUiThread(() -> Toast.makeText(this, "글 등록 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show());
                }
            }).start();
        });
    }
}

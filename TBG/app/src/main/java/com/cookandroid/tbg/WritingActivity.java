package com.cookandroid.tbg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WritingActivity extends AppCompatActivity {

    private static final String TAG = "WritingActivity";
    private static final String CREATE_POST_URL = "http://43.201.243.50:8080/kch_server/CreatePost";
    private static final String UPDATE_POST_URL = "http://43.201.243.50:8080/kch_server/UpdatePost";

    private Uri selectedMediaUri;
    private boolean isEditMode = false;
    private String postNum;
    private String existingImageUrl;
    private String existingVideoUrl;
    private ImageView selectedImageView;
    private VideoView selectedVideoView;
    private EditText titleInput, contentInput;
    private RadioGroup categoryGroup;
    private Map<Integer, String> categoryMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noticeboard_writing);

        initViews();
        setupCategoryMap();
        handleEditMode();
        setupListeners();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedMediaUri != null) {
            outState.putString("selectedMediaUri", selectedMediaUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String uriString = savedInstanceState.getString("selectedMediaUri");
        if (uriString != null) {
            selectedMediaUri = Uri.parse(uriString);
            loadSelectedMedia();
        }
    }

    private void initViews() {
        titleInput = findViewById(R.id.titleInput);
        contentInput = findViewById(R.id.contentInput);
        categoryGroup = findViewById(R.id.categoryGroup);
        selectedImageView = findViewById(R.id.selectedImageView);
        selectedVideoView = findViewById(R.id.selectedVideoView);

        selectedImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        selectedImageView.setAdjustViewBounds(true);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(selectedVideoView);
        selectedVideoView.setMediaController(mediaController);
    }

    private void setupCategoryMap() {
        categoryMap = new HashMap<>();
        categoryMap.put(R.id.category1, "A"); // 관광명소
        categoryMap.put(R.id.category2, "B"); // 교통수단
        categoryMap.put(R.id.category3, "C"); // 호텔
        categoryMap.put(R.id.category4, "D"); // 자유게시판
    }

    private void handleEditMode() {
        isEditMode = getIntent().getBooleanExtra("isEditMode", false);
        if (isEditMode) {
            postNum = getIntent().getStringExtra("postNum");
            titleInput.setText(getIntent().getStringExtra("title"));
            contentInput.setText(getIntent().getStringExtra("content"));

            String category = getIntent().getStringExtra("category");
            for (Map.Entry<Integer, String> entry : categoryMap.entrySet()) {
                if (entry.getValue().equals(category)) {
                    categoryGroup.check(entry.getKey());
                    break;
                }
            }

            existingImageUrl = getIntent().getStringExtra("postImageUrl");
            if (existingImageUrl != null) {
                existingImageUrl = existingImageUrl.replace("localhost", "10.0.2.2");
                displayImage(existingImageUrl);
            }
            existingVideoUrl = getIntent().getStringExtra("postVideoUrl");
            if (existingVideoUrl != null) {
                existingVideoUrl = existingVideoUrl.replace("localhost", "10.0.2.2");
            }

            Log.d(TAG, "Updated Video URL: " + existingVideoUrl);
            loadMedia(existingImageUrl, existingVideoUrl);

            ImageButton registerButton = findViewById(R.id.Post_Register);
            registerButton.setImageResource(R.drawable.postreok); // 수정 버튼 아이콘 변경
        }
    }

    private void loadMedia(String postImageUrl, String postVideoUrl) {
        if (postVideoUrl != null && !postVideoUrl.isEmpty()) {
            Log.d(TAG, "Loading Video URL: " + postVideoUrl);
            displayVideo(postVideoUrl);
        } else if (postImageUrl != null && !postImageUrl.isEmpty()) {
            Log.d(TAG, "Loading Image URL: " + postImageUrl);
            displayImage(postImageUrl);
        } else {
            hideMedia();
        }
    }

    private void displayVideo(String videoUrl) {
        selectedVideoView.setVisibility(View.VISIBLE);
        selectedImageView.setVisibility(View.GONE);
        Uri videoUri = Uri.parse(videoUrl);
        selectedVideoView.setVideoURI(videoUri);
        selectedVideoView.setOnPreparedListener(mp -> selectedVideoView.start());
    }

    private void displayImage(String imageUrl) {
        selectedImageView.setVisibility(View.VISIBLE);
        selectedVideoView.setVisibility(View.GONE);
        Glide.with(this).load(imageUrl).into(selectedImageView); // Glide로 이미지 로드
    }


    private void hideMedia() {
        selectedImageView.setVisibility(View.GONE);
        selectedVideoView.setVisibility(View.GONE);
    }

    private void loadSelectedMedia() {
        String mimeType = getContentResolver().getType(selectedMediaUri);
        if (mimeType != null && mimeType.startsWith("image")) {
            // 이미지 선택 시 비디오 초기화
            selectedVideoView.setVideoURI(null);
            selectedVideoView.setVisibility(View.GONE);
            selectedImageView.setImageURI(selectedMediaUri);
            selectedImageView.setVisibility(View.VISIBLE);

            existingVideoUrl = null; // 기존 비디오 URL 초기화
        } else if (mimeType != null && mimeType.startsWith("video")) {
            // 비디오 선택 시 이미지 초기화
            selectedImageView.setImageDrawable(null);
            selectedImageView.setVisibility(View.GONE);
            selectedVideoView.setVideoURI(selectedMediaUri);
            selectedVideoView.setVisibility(View.VISIBLE);

            existingImageUrl = null; // 기존 이미지 URL 초기화
        }
    }


    private void setupListeners() {
        findViewById(R.id.Post_cancle).setOnClickListener(v -> finish());
        findViewById(R.id.backspace).setOnClickListener(v -> finish());

        ImageButton uploadButton = findViewById(R.id.uploadButton);
        ActivityResultLauncher<Intent> mediaPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleMediaSelection(result.getData())
        );
        uploadButton.setOnClickListener(v -> launchMediaPicker(mediaPickerLauncher));

        ImageButton registerButton = findViewById(R.id.Post_Register);
        registerButton.setOnClickListener(v -> handlePostSubmission());
    }

    private void launchMediaPicker(ActivityResultLauncher<Intent> launcher) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        launcher.launch(intent);
    }

    private void handleMediaSelection(Intent data) {
        if (data != null) {
            selectedMediaUri = data.getData(); // 새로 선택한 URI
            if (selectedMediaUri != null) {
                Log.d(TAG, "Selected Media URI: " + selectedMediaUri);

                // MIME 타입 확인 후 이미지 또는 비디오 로드
                String mimeType = getContentResolver().getType(selectedMediaUri);
                if (mimeType != null && mimeType.startsWith("image")) {
                    displayImage(selectedMediaUri.toString()); // 이미지 표시
                } else if (mimeType != null && mimeType.startsWith("video")) {
                    displayVideo(selectedMediaUri.toString()); // 비디오 표시
                }
            }
        } else {
            Log.e(TAG, "Media selection failed or data is null.");
        }
    }


    private void handlePostSubmission() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String sessionId = sharedPreferences.getString("sessionId", null);
        if (sessionId == null) {
            showToast("세션 ID를 찾을 수 없습니다. 다시 로그인하세요.");
            return;
        }

        String title = titleInput.getText().toString().trim();
        String content = contentInput.getText().toString().trim();
        int selectedCategoryId = categoryGroup.getCheckedRadioButtonId();
        String selectedCategory = categoryMap.get(selectedCategoryId);

        if (title.isEmpty() || content.isEmpty() || selectedCategory == null) {
            showToast("모든 필드를 작성해주세요.");
            return;
        }

        new Thread(() -> submitPost(sessionId, title, content, selectedCategory)).start();
    }

    private void submitPost(String sessionId, String title, String content, String category) {
        try {
            String apiUrl = isEditMode ? UPDATE_POST_URL : CREATE_POST_URL;
            String boundary = "----WebKitFormBoundary" + UUID.randomUUID();
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod(isEditMode ? "PUT" : "POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setDoOutput(true);

            try (OutputStream os = new BufferedOutputStream(conn.getOutputStream())) {
                writeMultipartData(os, boundary, sessionId, title, content, category);

                if (selectedMediaUri != null) {
                    writeMediaData(os, boundary); // 새로 선택한 미디어 업로드
                } else if (isEditMode) {
                    // 기존 미디어 유지 정보를 전달
                    if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                        writeExistingMediaData(os, boundary, "image", existingImageUrl);
                    }
                    if (existingVideoUrl != null && !existingVideoUrl.isEmpty()) {
                        writeExistingMediaData(os, boundary, "video", existingVideoUrl);
                    }
                }


                os.write(("--" + boundary + "--\r\n").getBytes());
            }

            handleServerResponse(conn);
        } catch (Exception e) {
            Log.e(TAG, "Error occurred while submitting post", e);
            runOnUiThread(() -> showToast("글 등록 중 오류가 발생했습니다."));
        }
    }



    private void writeExistingMediaData(OutputStream os, String boundary, String mediaType, String existingMediaUrl) throws IOException {
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        os.write((twoHyphens + boundary + lineEnd).getBytes());
        os.write(("Content-Disposition: form-data; name=\"" + mediaType + "\"; filename=\"" + existingMediaUrl + "\"" + lineEnd).getBytes());
        os.write(("Content-Type: text/plain" + lineEnd + lineEnd).getBytes());
        os.write((existingMediaUrl + lineEnd).getBytes());
    }



    private void writeMultipartData(OutputStream os, String boundary, String sessionId, String title, String content, String category) throws IOException {
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        os.write((twoHyphens + boundary + lineEnd).getBytes());
        os.write(("Content-Disposition: form-data; name=\"sessionId\"" + lineEnd + lineEnd + sessionId + lineEnd).getBytes());
        os.write((twoHyphens + boundary + lineEnd).getBytes());
        os.write(("Content-Disposition: form-data; name=\"title\"" + lineEnd + lineEnd + title + lineEnd).getBytes());
        os.write((twoHyphens + boundary + lineEnd).getBytes());
        os.write(("Content-Disposition: form-data; name=\"content\"" + lineEnd + lineEnd + content + lineEnd).getBytes());
        os.write((twoHyphens + boundary + lineEnd).getBytes());
        os.write(("Content-Disposition: form-data; name=\"category\"" + lineEnd + lineEnd + category + lineEnd).getBytes());

        // 수정 요청인 경우 postNum 추가
        if (isEditMode) {
            os.write((twoHyphens + boundary + lineEnd).getBytes());
            os.write(("Content-Disposition: form-data; name=\"postNum\"" + lineEnd + lineEnd + postNum + lineEnd).getBytes());

            // 기존 이미지와 비디오 URL 전송
            if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                os.write((twoHyphens + boundary + lineEnd).getBytes());
                os.write(("Content-Disposition: form-data; name=\"existingImageUrl\"" + lineEnd + lineEnd + existingImageUrl + lineEnd).getBytes());
            }
            if (existingVideoUrl != null && !existingVideoUrl.isEmpty()) {
                os.write((twoHyphens + boundary + lineEnd).getBytes());
                os.write(("Content-Disposition: form-data; name=\"existingVideoUrl\"" + lineEnd + lineEnd + existingVideoUrl + lineEnd).getBytes());
            }
        }
    }


    private void writeMediaData(OutputStream os, String boundary) throws IOException {
        try (InputStream inputStream = getContentResolver().openInputStream(selectedMediaUri)) {
            String mimeType = getContentResolver().getType(selectedMediaUri);
            String fileName = UUID.randomUUID().toString() + "." + (mimeType != null ? mimeType.split("/")[1] : "jpg");

            os.write(("--" + boundary + "\r\n").getBytes());
            os.write(("Content-Disposition: form-data; name=\"media\"; filename=\"" + fileName + "\"\r\n").getBytes());
            os.write(("Content-Type: " + mimeType + "\r\n\r\n").getBytes());

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.write("\r\n".getBytes());
        }
    }

    private void handleServerResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                responseCode == HttpURLConnection.HTTP_OK ? conn.getInputStream() : conn.getErrorStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            Log.e(TAG, "Server Response: " + response);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                JSONObject jsonResponse = new JSONObject(response.toString());
                String updatedImageUrl = jsonResponse.optString("postImageUrl", "");
                String updatedVideoUrl = jsonResponse.optString("postVideoUrl", "");

                // localhost를 10.0.2.2로 변경
                if (updatedImageUrl.contains("localhost")) {
                    updatedImageUrl = updatedImageUrl.replace("localhost", "10.0.2.2");
                }
                if (updatedVideoUrl.contains("localhost")) {
                    updatedVideoUrl = updatedVideoUrl.replace("localhost", "10.0.2.2");
                }

                Log.e(TAG, "Final Updated Video URL: " + updatedVideoUrl);
                Log.e(TAG, "Updated Image URL: " + updatedImageUrl);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("title", titleInput.getText().toString());
                resultIntent.putExtra("content", contentInput.getText().toString());
                resultIntent.putExtra("postImageUrl", updatedImageUrl);
                resultIntent.putExtra("postVideoUrl", updatedVideoUrl);

                setResult(RESULT_OK, resultIntent);
                finish();

            } else {
                runOnUiThread(() -> showToast("서버 오류 발생"));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error occurred while processing server response", e);
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

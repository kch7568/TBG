package com.cookandroid.tbg;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private WebSocketClient webSocketClient;
    private RecyclerView commentRecyclerView;
    private PostCommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText commentInput;
    private ImageButton commentSubmitButton, commentCancelButton, likeButton;
    private ImageView profileImageView, postImageView;
    private VideoView postVideoView;

    private String postNum;
    private String sessionId = "";
    private String profileImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);

        // 뒤로가기 클릭 시 돌아가기
        Button backbtn = findViewById(R.id.backspace);
        backbtn.setOnClickListener(v -> finish());

        // ScrollView와 FloatingActionButton 참조
        ScrollView scrollView = findViewById(R.id.scrollpost); // ScrollView의 ID
        FloatingActionButton scrollToBottomButton = findViewById(R.id.scrollToBottomButton); // 버튼의 ID

        // FloatingActionButton 클릭 이벤트 추가
        scrollToBottomButton.setOnClickListener(view -> {
            // ScrollView 전체 높이를 기준으로 스크롤
            scrollView.post(() -> scrollView.smoothScrollTo(0, scrollView.getChildAt(0).getHeight()));
        });



        // 세션 ID 및 프로필 이미지 URL 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        sessionId = sharedPreferences.getString("sessionId", null);

        // Intent에서 데이터 가져오기
        postNum = getIntent().getStringExtra("postNum");
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String date = getIntent().getStringExtra("date");
        String content = getIntent().getStringExtra("content");
        String postImageUrl = getIntent().getStringExtra("postImageUrl");
        String postVideoUrl = getIntent().getStringExtra("postVideoUrl");
        String profileImageUrl = getIntent().getStringExtra("profileImageUrl"); // 작성자의 프로필 이미지 URL 받아오기
        Log.d("PostDetailActivity", "Received Profile Image URL: " + profileImageUrl);

        int views = getIntent().getIntExtra("views", 0);
        int likes = getIntent().getIntExtra("likes", 0);

        // localhost 주소 변환
        profileImageUrl = profileImageUrl.replace("localhost", "10.0.2.2");
        postImageUrl = postImageUrl.replace("localhost", "10.0.2.2");
        postVideoUrl = postVideoUrl.replace("localhost", "10.0.2.2");

        // UI 요소 설정
        TextView titleTextView = findViewById(R.id.titleInput);
        TextView authorTextView = findViewById(R.id.postAuthor);
        TextView dateTextView = findViewById(R.id.postCreateDay);
        TextView contentTextView = findViewById(R.id.contentInput);
        TextView viewsTextView = findViewById(R.id.postViews);
        TextView likesTextView = findViewById(R.id.postLikes);
        postImageView = findViewById(R.id.selectedImageView);
        profileImageView = findViewById(R.id.postAuthorProfileImage);
        postVideoView = findViewById(R.id.selectedVideoView);
        likeButton = findViewById(R.id.customLikeButton);

        titleTextView.setText(title);
        authorTextView.setText("작성자: " + (author != null ? author : "정보 없음"));
        dateTextView.setText(date);
        contentTextView.setText(content != null && !content.isEmpty() ? content : "내용 없음");
        viewsTextView.setText("조회수: " + views);
        likesTextView.setText("좋아요: " + likes);

        // 이미지 설정
        if (postImageUrl != null && !postImageUrl.isEmpty()) {
            postImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(postImageUrl).into(postImageView);
        } else {
            postImageView.setVisibility(View.GONE);
        }

        // profileImageUrl이 null 또는 빈 문자열이 아닌지 확인 후 설정
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Glide.with(this).load(profileImageUrl).into(profileImageView);
        } else {
            // 기본 프로필 이미지 설정
            profileImageView.setImageResource(R.drawable.default_profile_image);  // 기본 이미지 리소스 추가
        }


        // 비디오 설정
        if (postVideoUrl != null && !postVideoUrl.isEmpty()) {
            postVideoView.setVisibility(View.VISIBLE);
            Uri videoUri = Uri.parse(postVideoUrl);
            postVideoView.setVideoURI(videoUri);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(postVideoView);
            postVideoView.setMediaController(mediaController);
            postVideoView.setOnPreparedListener(mp -> {
                mp.setVolume(1.0f, 1.0f);
                postVideoView.start();
            });
        } else {
            postVideoView.setVisibility(View.GONE);
        }

        // 조회수 증가
        incrementViewCount(postNum);

        // 좋아요 버튼 클릭 이벤트
        likeButton.setOnClickListener(v -> likePost(postNum, sessionId, likesTextView));

        // 댓글 RecyclerView 초기화
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new PostCommentAdapter(this, commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        commentInput = findViewById(R.id.commentInput);
        commentSubmitButton = findViewById(R.id.commentSubmit);
        commentCancelButton = findViewById(R.id.commentCancel);

        initWebSocket();
        loadComments(postNum);

        // 댓글 작성 버튼 클릭 이벤트
        commentSubmitButton.setOnClickListener(v -> sendComment());

        commentCancelButton.setOnClickListener(v -> commentInput.setText(""));
    }

    private void initWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://10.0.2.2:8888/kch_server/commentWebSocket");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                runOnUiThread(() -> Toast.makeText(PostDetailActivity.this, "WebSocket 연결됨", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onMessage(String message) {
                runOnUiThread(() -> {
                    try {
                        JSONObject messageJson = new JSONObject(message);
                        String author = messageJson.getString("author");
                        String content = messageJson.getString("content");
                        String date = messageJson.getString("date");
                        String profileImageUrl = messageJson.optString("profileImageUrl", null);

                        // Comment 객체에 프로필 이미지 URL 포함하여 추가
                        commentList.add(new Comment(author, content, date, profileImageUrl));
                        commentAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                runOnUiThread(() -> Toast.makeText(PostDetailActivity.this, "WebSocket 연결 종료됨", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };
        webSocketClient.connect();
    }

    private void loadComments(String postNum) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8888/kch_server/GetComments?postId=" + postNum);
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

                    JSONArray commentsArray = new JSONArray(response.toString());
                    for (int i = 0; i < commentsArray.length(); i++) {
                        JSONObject commentObject = commentsArray.getJSONObject(i);
                        String author = commentObject.getString("author");
                        String content = commentObject.getString("content");
                        String date = commentObject.getString("date");
                        String profileImageUrl = commentObject.optString("profileImageUrl", null);

                        commentList.add(new Comment(author, content, date, profileImageUrl));
                    }

                    runOnUiThread(() -> commentAdapter.notifyDataSetChanged());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendComment() {
        String commentText = commentInput.getText().toString().trim();
        if (!commentText.isEmpty()) {
            JSONObject commentJson = new JSONObject();
            try {
                commentJson.put("postNum", postNum);
                commentJson.put("sessionId", sessionId);
                commentJson.put("content", commentText);

                // profileImageUrl이 null인지 체크 후 처리
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    commentJson.put("profileImageUrl", profileImageUrl);
                } else {
                    Log.e("sendComment", "profileImageUrl is null or empty, using default image.");
                    commentJson.put("profileImageUrl", ""); // 기본 이미지 URL로 대체할 수도 있음
                }

                Log.d("sendComment", "Sending comment JSON: " + commentJson.toString());
                webSocketClient.send(commentJson.toString());
            } catch (JSONException e) {
                Log.e("sendComment", "JSONException occurred: " + e.getMessage());
                e.printStackTrace();
            }
            commentInput.setText("");
            Toast.makeText(PostDetailActivity.this, "댓글이 전송되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(PostDetailActivity.this, "댓글 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void incrementViewCount(String postNum) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8888/kch_server/PostInteraction");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                String data = "action=incrementView&postNum=" + postNum + "&sessionId=" + sessionId;
                try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
                    writer.write(data);
                    writer.flush();
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("incrementViewCount", "View count incremented successfully.");
                } else {
                    Log.e("incrementViewCount", "Failed to increment view count.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void likePost(String postNum, String sessionId, TextView likesTextView) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8888/kch_server/PostInteraction");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                String data = "action=likePost&postNum=" + postNum + "&sessionId=" + sessionId;
                try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
                    writer.write(data);
                    writer.flush();
                }

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
                    boolean success = jsonResponse.getBoolean("success");
                    String message = jsonResponse.getString("message");

                    runOnUiThread(() -> {
                        Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (success) {
                            int currentLikes = Integer.parseInt(likesTextView.getText().toString().replace("좋아요: ", ""));
                            likesTextView.setText("좋아요: " + (currentLikes + 1));
                        }
                    });
                } else {
                    Log.e("likePost", "Failed to increment like count.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}

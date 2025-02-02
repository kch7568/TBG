package com.cookandroid.tbg;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private WebSocketClient webSocketClient;
    private RecyclerView commentRecyclerView;
    private PostCommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText commentInput;
    private ImageButton commentSubmitButton, commentCancelButton;
    private LottieAnimationView likeButton, starButton;

    private ImageView profileImageView, postImageView;
    private VideoView postVideoView;

    private String postNum;
    private String sessionId = "";
    private String profileImageUrl = "";
    private String currentUserId = "";

    private String authorId = "";

    private ImageButton kebabMenu;

    // 멤버 변수 선언
    private String title=  "";
    private String content = "";
    private String postImageUrl = "";
    private String postVideoUrl = "";

    private boolean isFavoriteProcessing = false; // 요청 처리 상태

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);

        // 뒤로가기 클릭 시 돌아가기
        Button backbtn = findViewById(R.id.backspace);
        backbtn.setOnClickListener(v -> finish());

        // Intent에서 데이터 가져오기
        postNum = getIntent().getStringExtra("postNum");
        title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String date = getIntent().getStringExtra("date");
        content = getIntent().getStringExtra("content");
        postImageUrl = getIntent().getStringExtra("postImageUrl").replace("localhost", "10.0.2.2");
        postVideoUrl = getIntent().getStringExtra("postVideoUrl").replace("localhost", "10.0.2.2");

        String profileImageUrl = getIntent().getStringExtra("profileImageUrl").replace("localhost", "10.0.2.2"); // 작성자의 프로필 이미지 URL 받아오기
        String currentUserId = ""; // 현재 로그인된 사용자 ID

        Log.d("PostDetailActivity", "Received postImageUrl: " + postImageUrl);
        Log.d("PostDetailActivity", "Received postVideoUrl: " + postVideoUrl);


        // **authorId 가져오기**
        authorId = getIntent().getStringExtra("authorId");




        // 세션 ID 및 프로필 이미지 URL 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        sessionId = sharedPreferences.getString("sessionId", null);

        // 현재 로그인 사용자 ID와 비교
        fetchCurrentUserId(authorId);

        Log.e("상세 테스트", "postNum: " + postNum);
        Log.e("상세 테스트", "title: " + title);
        Log.e("상세 테스트", "content: " + content);
        Log.e("상세 테스트", "currentUserId: " + currentUserId);


        // 케밥 메뉴 클릭 이벤트
        kebabMenu = findViewById(R.id.kebabMenu);
        // 케밥 메뉴 클릭 이벤트 내부



        // 케밥 메뉴에서 수정/삭제 선택 시 데이터 전달
        kebabMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(PostDetailActivity.this, kebabMenu);
            popupMenu.getMenuInflater().inflate(R.menu.kebab_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit_post) {
                    // 수정 기능
                    Intent intent = new Intent(PostDetailActivity.this, WritingActivity.class);
                    intent.putExtra("isEditMode", true);
                    intent.putExtra("postNum", postNum);
                    intent.putExtra("title", title); // 최신 제목 전달
                    intent.putExtra("content", content); // 최신 내용 전달
                    intent.putExtra("postImageUrl", postImageUrl); // 최신 이미지 URL 전달
                    intent.putExtra("postVideoUrl", postVideoUrl); // 최신 비디오 URL 전달

                    Log.d("PostDetailToEdit", "postImageUrl: " + postImageUrl);
                    Log.d("PostDetailToEdit", "postVideoUrl: " + postVideoUrl);

                    editPostLauncher.launch(intent); // 데이터 반환받음
                } else if (item.getItemId() == R.id.delete_post) {
                    // 삭제 기능
                    new Thread(() -> {
                        // AlertDialog 생성
                        runOnUiThread(() -> {
                            new AlertDialog.Builder(PostDetailActivity.this)
                                    .setTitle("게시글 삭제")
                                    .setMessage("정말 삭제하시겠습니까?")
                                    .setPositiveButton("예", (dialog, which) -> {
                                        // 예를 누르면 삭제 작업 진행
                                        new Thread(() -> {
                                            try {
                                                URL url = new URL("http://43.201.243.50:8080/kch_server/DeletePost?postNum=" + postNum + "&sessionId=" + sessionId);
                                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                                conn.setRequestMethod("DELETE");

                                                // 응답 코드 확인
                                                int responseCode = conn.getResponseCode();
                                                Log.d("DeletePostResponse", "Response Code: " + responseCode);

                                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                                    runOnUiThread(() -> {
                                                        Toast.makeText(PostDetailActivity.this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                        finish(); // 삭제 후 페이지 종료
                                                    });
                                                } else {
                                                    runOnUiThread(() -> Toast.makeText(PostDetailActivity.this, "게시글 삭제 실패.", Toast.LENGTH_SHORT).show());
                                                }
                                            } catch (Exception e) {
                                                Log.e("DeletePostRequest", "Error during deletion", e);
                                                runOnUiThread(() -> Toast.makeText(PostDetailActivity.this, "서버와의 통신 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show());
                                            }
                                        }).start();
                                    })
                                    .setNegativeButton("아니요", (dialog, which) -> {
                                        // 아니요를 누르면 아무 작업도 하지 않음
                                        dialog.dismiss();
                                    })
                                    .show();
                        });
                    }).start();



                }
                return true;
            });
            popupMenu.show();
        });



        // ScrollView와 FloatingActionButton 참조
        ScrollView scrollView = findViewById(R.id.scrollpost); // ScrollView의 ID
        FloatingActionButton scrollToBottomButton = findViewById(R.id.scrollToBottomButton); // 버튼의 ID

        // FloatingActionButton 클릭 이벤트 추가
        scrollToBottomButton.setOnClickListener(view -> {
            // ScrollView 전체 높이를 기준으로 스크롤
            scrollView.post(() -> scrollView.smoothScrollTo(0, scrollView.getChildAt(0).getHeight()));







        });


        Log.d("PostDetailActivity", "Received Profile Image URL: " + profileImageUrl);

        int views = getIntent().getIntExtra("views", 0);
        int likes = getIntent().getIntExtra("likes", 0);



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
        likeButton.setProgress(1f); // 애니메이션 마지막 프레임으로 설정
        starButton = findViewById(R.id.customStarButton);
        starButton.setProgress(0f); // 애니메이션 첫 프레임으로 설정


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
        // 즐겨찾기 버튼 클릭 이벤트


        // 즐겨찾기 상태 확인
        checkFavoriteStatus(postNum, sessionId);

        starButton.setOnClickListener(v -> toggleFavorite(postNum, sessionId));



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

    private final ActivityResultLauncher<Intent> editPostLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String updatedTitle = data.getStringExtra("title");
                    String updatedContent = data.getStringExtra("content");
                    String updatedImageUrl = data.getStringExtra("postImageUrl");
                    String updatedVideoUrl = data.getStringExtra("postVideoUrl");

                    Log.d("PostDetailActivity", "Updated Image URL: " + updatedImageUrl);
                    Log.d("PostDetailActivity", "Updated Video URL: " + updatedVideoUrl);


                    // 최신 데이터를 멤버 변수에 반영
                    if (updatedTitle != null) title = updatedTitle;
                    if (updatedContent != null) content = updatedContent;
                    if (updatedImageUrl != null) postImageUrl = updatedImageUrl;
                    if (updatedVideoUrl != null) postVideoUrl = updatedVideoUrl;

                    Log.d("PostDetailActivity", "Updated Image URL: " + updatedImageUrl);
                    Log.d("PostDetailActivity", "Updated Video URL: " + updatedVideoUrl);

                    // UI 업데이트
                    updateUIAfterEdit(title, content, postImageUrl, postVideoUrl);
                }
            }
    );

    @Override
    protected void onResume() {
        super.onResume();
        if (webSocketClient == null || !webSocketClient.isOpen()) {
            initWebSocket(); // WebSocket 재연결
        }
    }


    private void updateUIAfterEdit(String title, String content, String imageUrl, String videoUrl) {
        TextView titleTextView = findViewById(R.id.titleInput);
        TextView contentTextView = findViewById(R.id.contentInput);

        titleTextView.setText(title);
        contentTextView.setText(content);

        updateMediaViews(imageUrl, videoUrl);
    }

    private void updateMediaViews(String imageUrl, String videoUrl) {
        postVideoView.stopPlayback(); // 기존 재생 상태 초기화
        postVideoView.setVideoURI(null); // 재생 중인 동영상 초기화

        if (imageUrl != null && !imageUrl.isEmpty()) {
            postImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageUrl).into(postImageView);
            postVideoView.setVisibility(View.GONE);
        } else if (videoUrl != null && !videoUrl.isEmpty()) {
            postVideoView.setVisibility(View.VISIBLE);
            Uri videoUri = Uri.parse(videoUrl);
            postVideoView.setVideoURI(videoUri);

            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(postVideoView);
            postVideoView.setMediaController(mediaController);

            postVideoView.setOnPreparedListener(mp -> postVideoView.start());
            postImageView.setVisibility(View.GONE);
        } else {
            postImageView.setVisibility(View.GONE);
            postVideoView.setVisibility(View.GONE);
        }
    }











    private void updateMediaViews() {
        postVideoView.stopPlayback(); // 기존 재생 상태 초기화
        postVideoView.setVideoURI(null);

        if (postImageUrl != null && !postImageUrl.isEmpty()) {
            postImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(postImageUrl).into(postImageView);
            postVideoView.setVisibility(View.GONE);
        } else if (postVideoUrl != null && !postVideoUrl.isEmpty()) {
            postVideoView.setVisibility(View.VISIBLE);
            Uri videoUri = Uri.parse(postVideoUrl); // 변환된 경로 사용
            postVideoView.setVideoURI(videoUri);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(postVideoView);
            postVideoView.setMediaController(mediaController);

            postVideoView.setOnPreparedListener(mp -> postVideoView.start());
            postImageView.setVisibility(View.GONE);
        } else {
            postImageView.setVisibility(View.GONE);
            postVideoView.setVisibility(View.GONE);
        }
    }










    // 현재 로그인 사용자 ID 가져오기
    private void fetchCurrentUserId(String authorId) {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String sessionId = sharedPreferences.getString("sessionId", null);
                if (sessionId == null) {
                    Log.e("fetchCurrentUserId", "Session ID is null. Cannot proceed with the request.");
                    runOnUiThread(() -> Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show());
                    return;
                }

                String encodedSessionId = URLEncoder.encode(sessionId, "UTF-8");
                URL url = new URL("http://43.201.243.50:8080/kch_server/GetCurrentUser?sessionId=" + encodedSessionId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (InputStream inputStream = conn.getInputStream();
                         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        JSONObject userJson = new JSONObject(response.toString());
                        currentUserId = userJson.optString("nickname", "Unknown User");

                        runOnUiThread(() -> {
                            if (currentUserId != null && authorId != null) {
                                compareAuthorWithCurrentUser(authorId);
                            } else {
                                Log.e("fetchCurrentUserId", "currentUserId or authorId is null");
                            }
                        });
                    }
                } else {
                    Log.e("fetchCurrentUserId", "Failed to fetch current user ID. Response code: " + responseCode);
                    runOnUiThread(() -> Toast.makeText(this, "서버와 통신 중 문제가 발생했습니다.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e("fetchCurrentUserId", "Error occurred while fetching user ID", e);
            }
        }).start();
    }


    // 작성자와 현재 사용자 비교
    private void compareAuthorWithCurrentUser(String authorId) {
        if (kebabMenu != null) { // 메뉴 버튼 초기화 확인
            if (currentUserId != null && currentUserId.equals(authorId)) {
                kebabMenu.setVisibility(View.VISIBLE); // 작성자와 동일하면 메뉴 표시
            } else {
                Log.e("케밥",currentUserId);
                Log.e("케밥",authorId);
                kebabMenu.setVisibility(View.GONE); // 작성자가 아니면 메뉴 숨김
            }
        } else {
            Log.e("compareAuthorWithCurrentUser", "kebabMenu is null");
        }
    }
    private void initWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://43.201.243.50:8080/kch_server/commentWebSocket");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("WebSocket", "WebSocket 연결됨");
            }

            @Override
            public void onMessage(String message) {
                runOnUiThread(() -> {
                    try {
                        JSONObject messageJson = new JSONObject(message);
                        String commentId = messageJson.getString("commentId"); // 고유한 댓글 ID
                        // 중복 확인 로직 추가
                        boolean isDuplicate = false;
                        for (Comment comment : commentList) {
                            if (comment.getCommentId().equals(commentId)) {
                                isDuplicate = true;
                                break;
                            }
                        }

                        if (!isDuplicate) {
                            String author = messageJson.getString("author");
                            String content = messageJson.getString("content");
                            String date = messageJson.getString("date");
                            String profileImageUrl = messageJson.optString("profileImageUrl", null);
                            String authorId = messageJson.getString("authorId");

                            // Comment 객체에 프로필 이미지 URL 포함하여 추가
                            commentList.add(new Comment(author, content, date, profileImageUrl, authorId, commentId));
                            commentAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("onMessage", "중복 댓글 무시: " + commentId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }


            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("WebSocket", "WebSocket 연결 종료됨. Code: " + code + ", Reason: " + reason);
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
                URL url = new URL("http://43.201.243.50:8080/kch_server/GetComments?postId=" + postNum);
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
                        String commentId = commentObject.getString("commentId");

                        // 중복 댓글 확인
                        boolean isDuplicate = false;
                        for (Comment comment : commentList) {
                            if (comment.getCommentId().equals(commentId)) {
                                isDuplicate = true;
                                break;
                            }
                        }

                        if (!isDuplicate) {
                            String author = commentObject.getString("author");
                            String content = commentObject.getString("content");
                            String date = commentObject.getString("date");
                            String profileImageUrl = commentObject.optString("profileImageUrl", null);
                            String authorId = commentObject.getString("authorId");
                            commentList.add(new Comment(author, content, date, profileImageUrl, authorId, commentId));
                        }
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

                // **푸시 알림 요청 추가**
                sendPushNotificationRequest(commentText);

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

    private void sendPushNotificationRequest(String commentText) {
        new Thread(() -> {
            try {
                // 서버 URL
                URL url = new URL("http://43.201.243.50:8080/kch_server/SendPushNotification");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // 요청 JSON 데이터 생성
                JSONObject requestJson = new JSONObject();
                requestJson.put("postNum", postNum); // 게시글 번호
                requestJson.put("sessionId", sessionId); // 현재 세션, 이걸로 게시글작성자랑 비교후 자기글에 자기가 달을때 알림방지
                requestJson.put("commentContent", commentText); // 댓글 내용

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(requestJson.toString());
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("sendPushNotification", "Push notification request succeeded.");
                } else {
                    Log.e("sendPushNotification", "Failed to send push notification. Response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("sendPushNotification", "Error occurred while sending push notification.", e);
            }
        }).start();
    }

    private void incrementViewCount(String postNum) {
        new Thread(() -> {
            try {
                URL url = new URL("http://43.201.243.50:8080/kch_server/PostInteraction");
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
        runOnUiThread(() -> {
            if (!likeButton.isAnimating()) {
                likeButton.setProgress(0f); // 애니메이션을 처음부터 시작
                likeButton.playAnimation(); // 애니메이션 실행
            }
        });
        new Thread(() -> {
            try {
                URL url = new URL("http://43.201.243.50:8080/kch_server/PostInteraction");
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








    private void toggleFavorite(String postNum, String sessionId) {
        if (isFavoriteProcessing) {
            Log.d("toggleFavorite", "Request is already processing, ignoring this click.");
            return;
        }

        isFavoriteProcessing = true; // 요청 처리 중 상태로 변경

        boolean isStarred = starButton.getProgress() >= 0.9f; // 0.9 이상이면 '추가 상태'로 판단

        Log.d("toggleFavorite", "isStarred: " + isStarred + ", Progress: " + starButton.getProgress());

        if (isStarred) {
            Log.d("toggleFavorite", "Calling removeFromFavorites");
            removeFromFavorites(postNum, sessionId);
        } else {
            Log.d("toggleFavorite", "Calling addToFavorites");
            addToFavorites(postNum, sessionId);
        }
    }






    private void addToFavorites(String postNum, String sessionId) {
        new Thread(() -> {
            try {
                URL url = new URL("http://43.201.243.50:8080/kch_server/FavoriteInteraction");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                String data = "action=addFavorite&postNum=" + postNum + "&sessionId=" + sessionId;
                try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
                    writer.write(data);
                    writer.flush();
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        starButton.setSpeed(1f); // 정방향 애니메이션 실행
                        starButton.playAnimation();
                        starButton.addAnimatorListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                starButton.setProgress(1f); // 애니메이션 종료 후 상태 유지
                                isFavoriteProcessing = false; // 요청 처리 완료 상태
                            }

                            @Override
                            public void onAnimationStart(Animator animation) {}

                            @Override
                            public void onAnimationCancel(Animator animation) {}

                            @Override
                            public void onAnimationRepeat(Animator animation) {}
                        });
                        Toast.makeText(PostDetailActivity.this, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        isFavoriteProcessing = false; // 요청 처리 실패 상태
                        Log.e("addToFavorites", "Failed to add favorite.");
                    });
                }
            } catch (Exception e) {
                isFavoriteProcessing = false; // 요청 중 오류 발생 시 상태 해제
                e.printStackTrace();
            }
        }).start();
    }






    private void removeFromFavorites(String postNum, String sessionId) {
        new Thread(() -> {
            try {
                URL url = new URL("http://43.201.243.50:8080/kch_server/FavoriteInteraction");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                String data = "action=removeFavorite&postNum=" + postNum + "&sessionId=" + sessionId;
                try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
                    writer.write(data);
                    writer.flush();
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        starButton.setSpeed(-1f); // 역방향 애니메이션 실행
                        starButton.playAnimation();
                        starButton.addAnimatorListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                starButton.setProgress(0f); // 상태 초기화
                                isFavoriteProcessing = false; // 요청 처리 완료 상태
                            }

                            @Override
                            public void onAnimationStart(Animator animation) {}

                            @Override
                            public void onAnimationCancel(Animator animation) {}

                            @Override
                            public void onAnimationRepeat(Animator animation) {}
                        });
                        Toast.makeText(PostDetailActivity.this, "즐겨찾기에서 제거되었습니다.", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        isFavoriteProcessing = false; // 요청 처리 실패 상태
                        Log.e("removeFromFavorites", "Failed to remove favorite.");
                    });
                }
            } catch (Exception e) {
                isFavoriteProcessing = false; // 요청 중 오류 발생 시 상태 해제
                e.printStackTrace();
            }
        }).start();
    }


    private void checkFavoriteStatus(String postNum, String sessionId) {
        new Thread(() -> {
            try {
                URL url = new URL("http://43.201.243.50:8080/kch_server/GetFavoriteStatusServlet?postNum=" + postNum + "&sessionId=" + sessionId);
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
                    boolean isFavorite = jsonResponse.getBoolean("isFavorite");

                    runOnUiThread(() -> {
                        if (isFavorite) {
                            starButton.setProgress(1f);
                            starButton.setSpeed(1f);
                            starButton.playAnimation();
                        } else {
                            starButton.setProgress(0f);
                            starButton.setSpeed(-1f);
                            starButton.playAnimation();
                        }
                        Log.d("checkFavoriteStatus", "StarButton Progress Updated: " + starButton.getProgress());
                    });


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

    private String getRealPathFromURI(Uri contentUri) {
        String filePath = null;
        String[] proj = {android.provider.MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
                filePath = cursor.getString(column_index);
            }
            cursor.close();
        }
        return filePath;
    }
}
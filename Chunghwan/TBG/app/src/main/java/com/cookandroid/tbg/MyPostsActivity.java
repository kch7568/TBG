package com.cookandroid.tbg;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends AppCompatActivity {

    private RecyclerView myPostsRecyclerView, myCommentsRecyclerView;
    private MyPostsAdapter myPostsAdapter;
    private MyCommentsAdapter myCommentsAdapter;
    private List<PostItem> myPostsList = new ArrayList<>();
    private List<Comment> myCommentsList = new ArrayList<>();
    private String sessionId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypost);

        Button backbtn = findViewById(R.id.backspace);
        backbtn.setOnClickListener(v -> finish());

        // SharedPreferences에서 sessionId 로드
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        sessionId = sharedPreferences.getString("sessionId", null);

        if (sessionId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        myPostsRecyclerView = findViewById(R.id.myPostsRecyclerView);
        myCommentsRecyclerView = findViewById(R.id.myCommentsRecyclerView);

        setupRecyclerViews();

        // Fetch user's posts and comments
        fetchMyPosts();
        fetchMyComments();
    }

    private void setupRecyclerViews() {
        myPostsAdapter = new MyPostsAdapter(this, myPostsList);
        myPostsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myPostsRecyclerView.setAdapter(myPostsAdapter);

        myCommentsAdapter = new MyCommentsAdapter(this, myCommentsList);
        myCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myCommentsRecyclerView.setAdapter(myCommentsAdapter);
    }

    private void fetchMyPosts() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8888/kch_server/GetMyPostsServlet");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("sessionId", sessionId);
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    parseMyPostsResponse(response.toString());
                } else {
                    showError("Failed to fetch posts. Response code: " + responseCode);
                }
            } catch (Exception e) {
                showError("Error fetching posts: " + e.getMessage());
            }
        }).start();
    }

    private void parseMyPostsResponse(String response) {
        try {
            JSONArray postsArray = new JSONArray(response);
            myPostsList.clear();

            for (int i = 0; i < postsArray.length(); i++) {
                JSONObject postJson = postsArray.getJSONObject(i);

                String postImageUrl = postJson.optString("postImageUrl", null);
                String postVideoUrl = postJson.optString("postVideoUrl", null);

                if (postImageUrl != null) {
                    postImageUrl = postImageUrl.replace("localhost", "10.0.2.2");
                }
                if (postVideoUrl != null) {
                    postVideoUrl = postVideoUrl.replace("localhost", "10.0.2.2");
                }

                PostItem post = new PostItem(
                        postJson.getInt("postId"),
                        postJson.getString("title"),
                        postJson.getString("nickname"),
                        postJson.getString("createDate"),
                        postJson.getString("content"),
                        postImageUrl,
                        postJson.optString("profileImageUrl", null).replace("localhost", "10.0.2.2"),
                        postVideoUrl,
                        postJson.getInt("views"),
                        postJson.getInt("likes"),
                        postJson.optString("author", "Unknown")
                );

                myPostsList.add(post);
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                if (myPostsList.isEmpty()) {
                    Toast.makeText(this, "게시글이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                myPostsAdapter.notifyDataSetChanged();
            });
        } catch (JSONException e) {
            showError("Error parsing posts: " + e.getMessage());
        }
    }


    public void fetchMyComments() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8888/kch_server/GetMyCommentsServlet");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("sessionId", sessionId);
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    parseMyCommentsResponse(response.toString());
                } else {
                    showError("Failed to fetch comments. Response code: " + responseCode);
                }
            } catch (Exception e) {
                showError("Error fetching comments: " + e.getMessage());
            }
        }).start();
    }


    private void parseMyCommentsResponse(String response) {
        try {
            JSONArray commentsArray = new JSONArray(response);
            myCommentsList.clear();

            if (commentsArray.length() == 0) {
                Log.d("MyPostsActivity", "댓글이 없습니다.");
            }

            for (int i = 0; i < commentsArray.length(); i++) {
                JSONObject commentJson = commentsArray.getJSONObject(i);

                Comment comment = new Comment(
                        commentJson.optString("relatedPostTitle", "제목 없음"),
                        commentJson.getString("content"),
                        commentJson.getString("createDate"),
                        "",
                        "",
                        commentJson.getString("commentId")
                );

                myCommentsList.add(comment);
            }

            new Handler(Looper.getMainLooper()).post(() -> myCommentsAdapter.notifyDataSetChanged());
        } catch (JSONException e) {
            showError("Error parsing comments: " + e.getMessage());
        }
    }


    private void showError(String errorMessage) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(MyPostsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            Log.e("MyPostsActivity", errorMessage);
        });
    }

}

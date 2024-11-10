package com.cookandroid.tbg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AssignmentFragment extends Fragment {

    private RecyclerView postsRecyclerView;
    private PostsAdapter postsAdapter;
    private List<PostItem> postItemList = new ArrayList<>();
    private static final String TAG = "AssignmentFragment";
    private String selectedCategoryCode = "전체"; // 기본값으로 전체 설정
    private SwipeRefreshLayout swipeRefreshLayout;

    public AssignmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_assignment, container, false);

        // Initialize the write button and set up a click listener
        Button writeButton = view.findViewById(R.id.writeButton);
        writeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WritingActivity.class);
            startActivity(intent);
        });

        // Initialize the SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Load posts for the selected category from the server
            fetchPostsFromServer(selectedCategoryCode);
        });

        // Initialize the RecyclerView
        postsRecyclerView = view.findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the adapter
        postsAdapter = new PostsAdapter(getContext(), postItemList);
        postsRecyclerView.setAdapter(postsAdapter);

        // Initialize the RadioGroup for category selection
        RadioGroup categoryGroup = view.findViewById(R.id.categoryGroup);
        RadioButton defaultRadioButton = view.findViewById(R.id.category5);
        defaultRadioButton.setChecked(true); // 기본값으로 "전체" 선택

        categoryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.category1) {
                selectedCategoryCode = "A"; // 관광명소
            } else if (checkedId == R.id.category2) {
                selectedCategoryCode = "B"; // 교통수단
            } else if (checkedId == R.id.category3) {
                selectedCategoryCode = "C"; // 호텔
            } else if (checkedId == R.id.category4) {
                selectedCategoryCode = "D"; // 자유게시판
            } else if (checkedId == R.id.category5) {
                selectedCategoryCode = "전체";
            }
            // Load posts for the selected category from the server
            swipeRefreshLayout.setRefreshing(true);
            fetchPostsFromServer(selectedCategoryCode);
        });

        // Load posts from the server (initially load all)
        swipeRefreshLayout.setRefreshing(true);
        fetchPostsFromServer(selectedCategoryCode);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh posts when returning to this fragment
        swipeRefreshLayout.setRefreshing(true);
        fetchPostsFromServer(selectedCategoryCode);
    }

    // Method to fetch posts from the server based on selected category
    private void fetchPostsFromServer(String categoryCode) {
        new Thread(() -> {
            try {
                // 기본 URL 설정
                StringBuilder urlString = new StringBuilder("http://10.0.2.2:8888/kch_server/GetPosts");

                // 카테고리가 '전체'가 아닐 경우 쿼리 파라미터 추가
                if (!"전체".equals(categoryCode)) {
                    urlString.append("?categoryCode=").append(categoryCode);
                }

                // URL 객체 생성
                URL url = new URL(urlString.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Server response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    Log.d(TAG, "Server response: " + response.toString());

                    JSONArray postArray = new JSONArray(response.toString());
                    postItemList.clear(); // Clear the list before adding new items

                    for (int i = 0; i < postArray.length(); i++) {
                        JSONObject postObject = postArray.getJSONObject(i);
                        String title = postObject.getString("title");
                        String nickname = postObject.getString("nickname"); // 닉네임 사용
                        String date = postObject.getString("date");
                        String postImageUrl = postObject.optString("postImageUrl", ""); // 첨부 이미지 없는 경우를 대비하여 기본값 처리
                        String profileImageUrl = postObject.optString("profileImageUrl", ""); // 프로필 이미지 없는 경우를 대비하여 기본값 처리
                        int views = postObject.getInt("views");
                        int likes = postObject.getInt("likes");

                        PostItem post = new PostItem(title, nickname, date, profileImageUrl, postImageUrl, views, likes);
                        postItemList.add(post);
                        Log.d(TAG, "Post added: " + post.getTitle());
                    }

                    Log.d(TAG, "Total posts added: " + postItemList.size());

                    // Update the UI on the main thread
                    getActivity().runOnUiThread(() -> {
                        postsAdapter.notifyDataSetChanged();
                        postsRecyclerView.scrollToPosition(0); // Ensure scrolling starts from the top
                        swipeRefreshLayout.setRefreshing(false); // Stop the refresh animation
                    });
                } else {
                    Log.e(TAG, "FetchPosts error, server returned: " + responseCode);
                    getActivity().runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false)); // Stop the refresh animation in case of error
                }
            } catch (Exception e) {
                Log.e(TAG, "Error during fetchPostsFromServer", e);
                getActivity().runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false)); // Stop the refresh animation in case of error
            }
        }).start();
    }
}

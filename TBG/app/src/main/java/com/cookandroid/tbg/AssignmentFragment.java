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

import androidx.appcompat.widget.SearchView;
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

    private androidx.appcompat.widget.SearchView searchView;
    private List<PostItem> filteredList = new ArrayList<>(); // 필터링된 리스트


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



// Adapter 초기화 시 filteredList 사용
        postsAdapter = new PostsAdapter(getContext(), filteredList);
        postsRecyclerView.setAdapter(postsAdapter);

        // Set up the adapter and handle item clicks
        postsAdapter.setOnItemClickListener(post -> {
            // Create an Intent to navigate to the PostDetailActivity
            Intent intent = new Intent(getActivity(), PostDetailActivity.class);

            // Add debug logs to check data being passed to PostDetailActivity
            Log.d("AssignmentFragment", "Post Num: " + post.getPostNum());
            Log.d("AssignmentFragment", "Title: " + post.getTitle());
            Log.d("AssignmentFragment", "Author: " + post.getNickname());
            Log.d("AssignmentFragment", "Date: " + post.getDate());
            Log.d("AssignmentFragment", "Content: " + post.getContent());
            Log.d("AssignmentFragment", "Post Image URL: " + post.getPostImageUrl());
            Log.d("AssignmentFragment", "Profile Image URL: " + post.getProfileImageUrl());
            Log.d("AssignmentFragment", "Views: " + post.getViews());
            Log.d("AssignmentFragment", "Likes: " + post.getLikes());

            intent.putExtra("postNum", String.valueOf(post.getPostNum()));

            intent.putExtra("title", post.getTitle());
            intent.putExtra("author", post.getNickname()); // 작성자 이름 전달
            intent.putExtra("date", post.getDate());
            intent.putExtra("content", post.getContent()); // 본문 내용 전달
            intent.putExtra("postImageUrl", post.getPostImageUrl()); // 게시글 이미지 URL 전달
            intent.putExtra("profileImageUrl", post.getProfileImageUrl()); // 프로필 이미지 URL 전달
            intent.putExtra("postVideoUrl", post.getVideoUrl());
            intent.putExtra("views", post.getViews());
            intent.putExtra("likes", post.getLikes());
            intent.putExtra("authorId",post.getAuthor());  //추가
            startActivity(intent);
        });




        SearchView searchView = view.findViewById(R.id.searchView);

        // SearchView 초기화

// SearchView 리스너 설정
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterPosts(query); // 텍스트 제출 시 필터링
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPosts(newText); // 텍스트 변경 시 필터링
                return false;
            }
        });




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

    private void filterPosts(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(postItemList);
        } else {
            for (PostItem post : postItemList) {
                if (post.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        post.getContent().toLowerCase().contains(query.toLowerCase()) ||
                        post.getNickname().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(post);
                }
            }
        }
        postsAdapter.notifyDataSetChanged();
    }



    // Method to fetch posts from the server based on selected category
    private void fetchPostsFromServer(String categoryCode) {
        new Thread(() -> {
            try {
                // 기본 URL 설정
                StringBuilder urlString = new StringBuilder("http://43.201.243.50:8080/kch_server/GetPosts");

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
                    filteredList.clear();

                    for (int i = 0; i < postArray.length(); i++) {
                        JSONObject postObject = postArray.getJSONObject(i);
                        // JSON 파싱 후 PostItem 객체 생성
                        int postNum = postObject.getInt("postNum");
                        String title = postObject.getString("title");
                        String nickname = postObject.getString("nickname");
                        String date = postObject.getString("date");
                        String content = postObject.optString("content", "내용 없음");
                        String postImageUrl = postObject.optString("postImageUrl", "");
                        String profileImageUrl = postObject.optString("profileImageUrl", "");
                        String videoUrl = postObject.optString("postVideoUrl", ""); // 추가된 videoUrl
                        String authorId = postObject.optString("authorId", "");  //추가
                        int views = postObject.getInt("views");
                        int likes = postObject.getInt("likes");

                        PostItem post = new PostItem(postNum, title, nickname, date, content, postImageUrl, profileImageUrl, videoUrl, views, likes, authorId);
                        postItemList.add(post);
                        filteredList.add(post); // 초기화 시 필터링 리스트에도 추가


                        Log.d(TAG, "Post added: " + post.getTitle());
                    }


                    Log.d(TAG, "Total posts added: " + postItemList.size());

                    // Update the UI on the main thread
                    getActivity().runOnUiThread(() -> {
                        Log.d("AssignmentFragment", "postItemList size: " + postItemList.size());
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

package com.cookandroid.tbg;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView favoritesRecyclerView;
    private FavoritesAdapter favoritesAdapter;
    private List<PostItem> favoritePosts;
    private static final String BASE_URL = "http://10.0.2.2:8888/kch_server/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        favoritePosts = new ArrayList<>();
        favoritesAdapter = new FavoritesAdapter(this, favoritePosts, this::removeFavorite);

        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        fetchFavoritePosts();

        // Back button handling
        findViewById(R.id.backspace).setOnClickListener(v -> finish());
    }

    private void fetchFavoritePosts() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String sessionId = sharedPreferences.getString("sessionId", null);

        if (sessionId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BASE_URL + "GetFavoritePosts?sessionId=" + sessionId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    favoritePosts.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject postObject = response.getJSONObject(i);
                            PostItem post = new PostItem(
                                    postObject.getInt("postNum"),
                                    postObject.getString("title"),
                                    postObject.getString("nickname"),
                                    postObject.getString("date"),
                                    postObject.getString("content"),
                                    postObject.optString("postImageUrl", "").replace("localhost", "10.0.2.2"),
                                    postObject.optString("profileImageUrl", "").replace("localhost", "10.0.2.2"),
                                    postObject.optString("postVideoUrl", "").replace("localhost", "10.0.2.2"),
                                    postObject.getInt("views"),
                                    postObject.getInt("likes"),
                                    postObject.getString("nickname")
                            );
                            favoritePosts.add(post);
                        }
                        favoritesAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("FavoritesActivity", "JSON Parsing Error: ", e);
                        Toast.makeText(this, "데이터 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("FavoritesActivity", "Error: " + error.getMessage());
                    Toast.makeText(this, "즐겨찾기 목록을 가져오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void removeFavorite(int postNum) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String sessionId = sharedPreferences.getString("sessionId", null);

        if (sessionId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String action = "removeFavorite";
        String url = BASE_URL + "FavoriteInteraction?sessionId=" + sessionId + "&postNum=" + postNum + "&action=" + action;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Toast.makeText(this, "즐겨찾기에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    fetchFavoritePosts();
                },
                error -> {
                    Log.e("FavoritesActivity", "Error: " + error.getMessage());
                    Toast.makeText(this, "삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(stringRequest);
    }
}

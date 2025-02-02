package com.cookandroid.tbg;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PostService {
    // 서버에서 좋아요 순으로 게시글 2개 가져오는 API 엔드포인트
    @GET("GetTopPosts")
    Call<List<PostItem>> getTopPosts();
}

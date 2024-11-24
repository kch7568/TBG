package com.example.test1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AirportService {

    // 공항 정보를 검색하는 API 요청 (CSV 형식)
    @GET("/15077093/v1/file-data-list")
    Call<String> getAirportInfo(  // 응답을 String으로 받기
                                  @Query("serviceKey") String serviceKey,  // API 인증 키
                                  @Query("type") String type,             // 요청 형식 (e.g., CSV로 변경)
                                  @Query("numOfRows") int numOfRows,      // 한 번에 가져올 데이터 개수
                                  @Query("pageNo") int pageNo             // 페이지 번호
    );
}

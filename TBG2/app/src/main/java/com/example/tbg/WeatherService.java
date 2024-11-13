package com.example.tbg;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    // OpenWeatherMap API에서 날씨 정보를 가져오는 GET 요청

    @GET("data/2.5/weather")
    Call<WeatherResponse> getWeatherData(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("units") String units
    );
}
package com.example.tbg;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {


    private static Retrofit retrofit = null;

    public static Retrofit getWeatherClient() {
        return new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit getGeocodingClient() {
        return new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl) //OpenWeatherMap API의 기본 URL
                    .addConverterFactory(GsonConverterFactory.create()) //JSON 데이터를 자동으로 Java 객체로 변환
                    .build();
        }
        return retrofit;
    }
    // 게시글 데이터를 위한 Retrofit 클라이언트 생성
    public static Retrofit getPostClient() {
        return new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8888/kch_server/") // 서버의 URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
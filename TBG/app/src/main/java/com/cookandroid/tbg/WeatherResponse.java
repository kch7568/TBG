package com.cookandroid.tbg;

import com.google.gson.annotations.SerializedName; //JSON 필드 이름과 Java 필드 이름이 다를 때 사용

public class WeatherResponse {

    @SerializedName("main")
    public Main main;

    @SerializedName("weather")
    public Weather[] weather;

    @SerializedName("dt")
    public long timestamp;

    public class Main {
        @SerializedName("temp") //온도
        public float temp;

        @SerializedName("humidity") //습도
        public int humidity;

        @SerializedName("pop")
        public float pop; // 강수 확률
    }

    public class Weather {
        @SerializedName("main") //날씨 상태
        public String mainCondition;

        @SerializedName("icon") //아이콘 정보
        public String icon;
    }
}
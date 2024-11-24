package com.example.test1;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import java.util.List;

public class AirportResponse {
    @SerializedName("data")
    private List<Airport> airports;

    public List<Airport> getAirports() {
        return airports;
    }

    public class Airport {
        @SerializedName("공항코드1(IATA)")
        private String airportCodeIATA;  // 공항 코드 (예: ADP)

        @SerializedName("한국공항명")
        private String koreanAirportName;  // 한국 공항명 (예: 암파라 공항)

        @SerializedName("한국국가명")
        private String koreanCountryName;  // 한국 국가명 (예: 스리랑카)

        // Getter 메서드들
        public String getAirportCodeIATA() {
            return airportCodeIATA;
        }
        public String getKoreanAirportName() {
            return koreanAirportName;
        }

        public String getKoreanCountryName() {
            return koreanCountryName;
        }

        public void setAirportCodeIATA(String airportCodeIATA) {
            this.airportCodeIATA = airportCodeIATA;
        }

        public void setKoreanAirportName(String koreanAirportName) {
            this.koreanAirportName = koreanAirportName;
        }

        public void setKoreanCountryName(String koreanCountryName) {
            this.koreanCountryName = koreanCountryName;
        }
    }
}

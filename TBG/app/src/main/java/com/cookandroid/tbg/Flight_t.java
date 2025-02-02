package com.cookandroid.tbg;

public class Flight_t {
    private String price;
    private String departureInfo;
    private String arrivalInfo;
    private String returnInfo; // 복귀 정보를 위한 필드

    public Flight_t(String price, String departureInfo, String arrivalInfo, String returnInfo) {
        this.price = price;
        this.departureInfo = departureInfo;
        this.arrivalInfo = arrivalInfo;
        this.returnInfo = returnInfo; // 복귀 정보 초기화
    }

    public String getPrice() {
        return price;
    }

    public String getDepartureInfo() {
        return departureInfo;
    }

    public String getArrivalInfo() {
        return arrivalInfo;
    }

    public String getReturnInfo() {
        return returnInfo;
    }
}

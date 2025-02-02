package com.cookandroid.tbg;

public class Flight {
    private final String title;  // 항공편 제목 (예: "ICN - KIX")
    private final String departureDate;  // 출발 날짜
    private final String departureTime;  // 출발 시간
    private final String returnDate;     // 복귀 날짜 (왕복일 경우)
    private final String returnTime;     // 복귀 시간 (왕복일 경우)
    private final String price;          // 항공편 가격
    private final String departureAirlineCode;  // 출발 항공사 코드
    private final String returnAirlineCode;     // 복귀 항공사 코드
    private final String departureAirlineName;  // 출발 항공사 이름
    private final String returnAirlineName;     // 복귀 항공사 이름
    private final String departureAirport;      // 출발 공항
    private final String arrivalAirport;        // 도착 공항

    // 생성자
    public Flight(String title, String departureDate, String departureTime,
                  String returnDate, String returnTime, String price,
                  String departureAirlineCode, String returnAirlineCode,
                  String departureAirlineName, String returnAirlineName,
                  String departureAirport, String arrivalAirport) {
        this.title = title;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.returnDate = returnDate;
        this.returnTime = returnTime;
        this.price = price;
        this.departureAirlineCode = departureAirlineCode;
        this.returnAirlineCode = returnAirlineCode;
        this.departureAirlineName = departureAirlineName;
        this.returnAirlineName = returnAirlineName;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
    }

    public boolean isRoundTrip() {
        return returnDate != null && !returnDate.isEmpty()
                && returnTime != null && !returnTime.isEmpty();
    }

    // Getter 메서드
    public String getTitle() { return title; }
    public String getDepartureDate() { return departureDate; }
    public String getDepartureTime() { return departureTime; }
    public String getReturnDate() { return returnDate; }
    public String getReturnTime() { return returnTime; }
    public String getPrice() { return price; }
    public String getDepartureAirlineCode() { return departureAirlineCode; }
    public String getReturnAirlineCode() { return returnAirlineCode; }
    public String getDepartureAirlineName() { return departureAirlineName; }
    public String getReturnAirlineName() { return returnAirlineName; }
    public String getDepartureAirport() { return departureAirport; }
    public String getArrivalAirport() { return arrivalAirport; }

    @Override
    public String toString() {
        return "Flight{" +
                "title='" + title + '\'' +
                ", departureDate='" + departureDate + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", returnDate='" + returnDate + '\'' +
                ", returnTime='" + returnTime + '\'' +
                ", price='" + price + '\'' +
                ", departureAirlineCode='" + departureAirlineCode + '\'' +
                ", returnAirlineCode='" + returnAirlineCode + '\'' +
                ", departureAirlineName='" + departureAirlineName + '\'' +
                ", returnAirlineName='" + returnAirlineName + '\'' +
                ", departureAirport='" + departureAirport + '\'' +
                ", arrivalAirport='" + arrivalAirport + '\'' +
                '}';
    }
}

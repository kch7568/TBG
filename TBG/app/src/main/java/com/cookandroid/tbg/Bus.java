package com.cookandroid.tbg;

public class Bus {
    private String departureStation;
    private String arrivalStation;
    private String departureDate;
    private String departureTime;
    private String arrivalDate;
    private String arrivalTime;
    private String price;
    private String seatClass;


    // 기본 생성자
    public Bus() {}

    // Getter 메서드
    public String getDepartureStation() { return departureStation; }
    public String getArrivalStation() { return arrivalStation; }
    public String getDepartureDate() { return departureDate; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalDate() { return arrivalDate; }
    public String getArrivalTime() { return arrivalTime; }
    public String getPrice() { return price; }
    public String getSeatClass() { return seatClass; }

    // Setter 메서드
    public void setDepartureStation(String departureStation) { this.departureStation = departureStation; }
    public void setArrivalStation(String arrivalStation) { this.arrivalStation = arrivalStation; }
    public void setDepartureDate(String departureDate) { this.departureDate = departureDate; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public void setArrivalDate(String arrivalDate) { this.arrivalDate = arrivalDate; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public void setPrice(String price) { this.price = price; }
    public void setSeatClass(String seatClass) { this.seatClass = seatClass; }

    @Override
    public String toString() {
        return "Bus{" +
                ", departureStation='" + departureStation + '\'' +
                ", arrivalStation='" + arrivalStation + '\'' +
                ", departureDate='" + departureDate + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", arrivalDate='" + arrivalDate + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", price='" + price + '\'' +
                ", seatClass='" + seatClass + '\'' +
                '}';
    }
}
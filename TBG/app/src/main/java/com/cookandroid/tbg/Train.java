package com.cookandroid.tbg;

public class Train {
    private String trainNo;
    private String departureStation;
    private String arrivalStation;
    private String departureDate;//
    private String departureTime;
    private String arrivalDate;//
    private String arrivalTime;
    private String trainType;
    private String price;
    private String nodeid;
    private String nodename;
    private String vehiclekndid;
    private String vehiclekndnm;

    // 기본 생성자
    public Train() {}

    // Getter 메서드
    public String getTrainNo() { return trainNo; }
    public String getDepartureStation() { return departureStation; }
    public String getArrivalStation() { return arrivalStation; }
    public String getDepartureDate() { return departureDate; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public String getArrivalDate() {return arrivalDate;}
    public String getTrainType() { return trainType; }
    public String getPrice() { return price; }
    public String getNodeid() { return nodeid; }
    public String getNodename() { return nodename; }
    public String getVehiclekndid() { return vehiclekndid; }
    public String getVehiclekndnm() { return vehiclekndnm; }

    // Setter 메서드
    public void setTrainNo(String trainNo) { this.trainNo = trainNo; }
    public void setDepartureStation(String departureStation) { this.departureStation = departureStation; }//출발지
    public void setArrivalStation(String arrivalStation) { this.arrivalStation = arrivalStation; }//도착지
    public void setDepartureDate(String departureDate) { this.departureDate = departureDate; }//출발일
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }//출발시간
    public void setArrivalDate(String arrivalDate){ this.arrivalDate = arrivalDate;}
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }//도착시간
    public void setTrainType(String trainType) { this.trainType = trainType; }
    public void setPrice(String price) { this.price = price; }
    public void setNodeid(String nodeid) { this.nodeid = nodeid; }
    public void setNodename(String nodename) { this.nodename = nodename; }
    public void setVehiclekndid(String vehiclekndid) { this.vehiclekndid = vehiclekndid; }
    public void setVehiclekndnm(String vehiclekndnm) { this.vehiclekndnm = vehiclekndnm; }

    @Override
    public String toString() {
        return "Train{" +
                "trainNo='" + trainNo + '\'' +
                ", departureStation='" + departureStation + '\'' +
                ", arrivalStation='" + arrivalStation + '\'' +
                ", departureDate='" + departureDate + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", trainType='" + trainType + '\'' +
                ", price='" + price + '\'' +
                ", nodeid='" + nodeid + '\'' +
                ", nodename='" + nodename + '\'' +
                ", vehiclekndid='" + vehiclekndid + '\'' +
                ", vehiclekndnm='" + vehiclekndnm + '\'' +
                '}';
    }
}
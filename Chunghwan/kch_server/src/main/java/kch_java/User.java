package kch_java;

public class User {
    private String ID = "";
    private String PW = "";
    private String NickName = "";

    public String getID() { return this.ID; }
    public String getPW() { return this.PW; }
    public String getNickName() { return this.NickName; }
    public void setID(String id) { this.ID = id; }
    public void setPW(String pw) { this.PW = pw; }
    public void setNickName(String nickname) { this.NickName = nickname; }
}

package kch_java;

public class User {
    private String ID = "";
    private String PW = "";
    private String NickName = "";
    private String PW_re ="";
    
    public String getID() { return this.ID; }
    public String getPW() { return this.PW; }
    public String getNickName() { return this.NickName; }
    public String getPW_re() {return this.PW_re;}
    public void setID(String id) { this.ID = id; }
    public void setPW(String pw) { this.PW = pw; }
    public void setNickName(String nickname) { this.NickName = nickname; }
    public void setPW_re(String pw_re) {this.PW_re = pw_re;}
}

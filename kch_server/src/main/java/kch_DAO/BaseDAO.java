package kch_DAO;
import java.sql.*;

public class BaseDAO {
    protected String driverName = "com.mysql.cj.jdbc.Driver";
    protected String url = "jdbc:mysql://tbg.cb6i02woqbxl.ap-northeast-2.rds.amazonaws.com:3306/TBG?autoReconnect=true";
    protected String username = "youjin3811";
    protected String password = "kk756868";
    
    protected Connection conn = null;

	//객체 생성시, 생성자에서 자동으로 TBG데이터베이스에 커넥션된다.
	public BaseDAO() throws SQLException, ClassNotFoundException {
        // JDBC 드라이버 로드
        Class.forName(driverName);
        // 데이터베이스 연결
        conn = DriverManager.getConnection(url, username, password);
    }

    // 데이터베이스 연결 해제 메서드
    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}

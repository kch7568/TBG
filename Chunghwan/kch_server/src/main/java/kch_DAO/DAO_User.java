package kch_DAO;

import java.sql.*;
import kch_java.User;

public class DAO_User extends BaseDAO {

	//객체 생성시, 생성자에서 자동으로 TBG데이터베이스에 커넥션된다.
	public DAO_User() throws SQLException, ClassNotFoundException {
        super();
    }
	
    // 비밀번호 업데이트 메서드
	public boolean updatePassword(User user) {
	    String sql = "UPDATE User SET Password = ? WHERE User_Id = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, user.getPW());  // 새 비밀번호 설정
	        pstmt.setString(2, user.getID());  // 해당 사용자 ID로 찾음
	        int updatedRows = pstmt.executeUpdate();
	        return updatedRows > 0;  // 업데이트 성공 여부 반환
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public User getUserById(String userId) {
	    String sql = "SELECT * FROM User WHERE User_Id = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, userId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // 사용자 정보가 존재하면 로그 출력
	            System.out.println("User found: " + rs.getString("User_Id"));
	            User user = new User();
	            user.setID(rs.getString("User_Id"));
	            user.setPW(rs.getString("Password"));
	            user.setNickName(rs.getString("Nickname"));
	            return user;
	        }
	        
	        // 사용자 정보가 없으면 로그 출력
	        System.out.println("No user found with ID: " + userId);
	        return null;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	public boolean isNickNameExists(String userNickName) {
	    String sql = "SELECT 1 FROM User WHERE Nickname = ?";  // 1을 SELECT하여 존재 여부만 확인
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, userNickName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // 닉네임이 존재하면 false 반환
	            return false;
	        } else {
	            // 닉네임이 없으면 true 반환
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;  // 예외가 발생하면 false 반환
	    }
	}


	
	
	
	
	public boolean addUser(User user) {
		String sql = "INSERT INTO User (User_Id, Password, Nickname) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getID());
            pstmt.setString(2, user.getPW());
            pstmt.setString(3, user.getNickName());
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;  // 삽입 성공 여부 반환
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
	}

}

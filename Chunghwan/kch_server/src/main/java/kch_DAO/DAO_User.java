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
	    String userSql = "INSERT INTO User (User_Id, Password, Nickname) VALUES (?, ?, ?)";
	    String alertSql = "INSERT INTO Alert (User_Id, Alert_Status) VALUES (?, ?)";

	    try {
	        conn.setAutoCommit(false); // 트랜잭션 시작

	        // User 테이블에 데이터 삽입
	        try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
	            userPstmt.setString(1, user.getID());
	            userPstmt.setString(2, user.getPW());
	            userPstmt.setString(3, user.getNickName());
	            int userRowsInserted = userPstmt.executeUpdate();

	            if (userRowsInserted > 0) {
	                // Alert 테이블에 데이터 삽입
	                try (PreparedStatement alertPstmt = conn.prepareStatement(alertSql)) {
	                    alertPstmt.setString(1, user.getID());
	                    alertPstmt.setBoolean(2, true); // 기본 Alert_Status 값 설정 (기본값: true)
	                    alertPstmt.executeUpdate();
	                }
	            }

	            conn.commit(); // 트랜잭션 커밋
	            return userRowsInserted > 0;
	        } catch (SQLException e) {
	            conn.rollback(); // 오류 시 롤백
	            e.printStackTrace();
	            return false;
	        } finally {
	            conn.setAutoCommit(true); // 자동 커밋 모드로 되돌림
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	
	// 프로필 이미지 URL 업서트 메서드
	public boolean upsertProfileImage(String userId, int postSize, String postExtension, String postUrl) {
	    System.out.println("Entering upsertProfileImage method");
	    String checkSql = "SELECT 1 FROM MyProfile WHERE User_Id = ?";
	    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
	        checkStmt.setString(1, userId);
	        ResultSet rs = checkStmt.executeQuery();
	        System.out.println("Query executed");

	        if (rs.next()) {
	            System.out.println("User exists, updating record...");
	            String updateSql = "UPDATE MyProfile SET My_postSize = ?, My_postextension = ?, My_postURL = ? WHERE User_Id = ?";
	            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
	                updateStmt.setInt(1, postSize);
	                updateStmt.setString(2, postExtension);
	                updateStmt.setString(3, postUrl);
	                updateStmt.setString(4, userId);
	                boolean result = updateStmt.executeUpdate() > 0;
	                System.out.println("Update result: " + result);
	                return result;
	            }
	        } else {
	            System.out.println("User does not exist, inserting new record...");
	            String insertSql = "INSERT INTO MyProfile (User_Id, My_postSize, My_postextension, My_postURL) VALUES (?, ?, ?, ?)";
	            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
	                insertStmt.setString(1, userId);
	                insertStmt.setInt(2, postSize);
	                insertStmt.setString(3, postExtension);
	                insertStmt.setString(4, postUrl);
	                boolean result = insertStmt.executeUpdate() > 0;
	                System.out.println("Insert result: " + result);
	                return result;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public String getProfileImageUrl(String userId) {
	    String sql = "SELECT My_postURL FROM MyProfile WHERE User_Id = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, userId);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("My_postURL");
	        } else {
	            return null;  // 기본 프로필 이미지가 없는 경우 null 반환
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	public boolean updateAlertStatus(String userId, boolean status) {
	    String sql = "UPDATE Alert SET Alert_Status = ? WHERE User_Id = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setBoolean(1, status);
	        pstmt.setString(2, userId);
	        int updatedRows = pstmt.executeUpdate();
	        return updatedRows > 0; // 업데이트 성공 여부 반환
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public Boolean getAlertStatus(String userId) {
	    String sql = "SELECT Alert_Status FROM Alert WHERE User_Id = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, userId);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getBoolean("Alert_Status");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // 실패 시 null 반환
	}

	public boolean deleteUser(String userId) {
	    String sql = "DELETE FROM User WHERE User_Id = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, userId);
	        int deletedRows = pstmt.executeUpdate();
	        return deletedRows > 0; // 삭제 성공 여부 반환
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


}








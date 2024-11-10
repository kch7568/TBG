package kch_DAO;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import kch_java.PostItem;
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
	
	// 게시물 저장 메서드
    public int savePost(String userId, String title, String content, String categoryCode) {
        String postSql = "INSERT INTO Post (User_Id, Post_Title, Post_Content, Category_Code, Post_createDay) VALUES (?, ?, ?, ?, NOW())";
        try (PreparedStatement postPstmt = conn.prepareStatement(postSql, Statement.RETURN_GENERATED_KEYS)) {
            postPstmt.setString(1, userId);
            postPstmt.setString(2, title);
            postPstmt.setString(3, content);
            postPstmt.setString(4, categoryCode);

            int affectedRows = postPstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Post creation failed, no rows affected.");
            }

            // 생성된 게시물 번호 가져오기
            try (ResultSet generatedKeys = postPstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Post creation failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
 // 파일 정보 저장 메서드
    public boolean saveFile(int postNum, String filePath, String fileName, String fileExtension, String fileCapacity) {
        String fileSql = "INSERT INTO File (Post_Num, File_Path, File_name, File_extension, File_capacity) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement filePstmt = conn.prepareStatement(fileSql)) {
            filePstmt.setInt(1, postNum);
            filePstmt.setString(2, filePath);
            filePstmt.setString(3, fileName);
            filePstmt.setString(4, fileExtension);
            filePstmt.setString(5, fileCapacity);

            int fileInsertedRows = filePstmt.executeUpdate();
            return fileInsertedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

	/**
	 * MIME 타입에 따라 파일 확장자를 결정하는 메소드
	 */
	private String determineFileExtension(String mimeType) {
	    switch (mimeType) {
	        case "image/jpeg":
	            return "jpg";
	        case "image/png":
	            return "png";
	        case "image/gif":
	            return "gif";
	        case "video/mp4":
	            return "mp4";
	        case "video/avi":
	            return "avi";
	        case "video/mkv":
	            return "mkv";
	        default:
	            return null; // 지원되지 않는 파일 유형
	    }
	}
	
	

	public List<PostItem> getAllPosts() {
	    List<PostItem> postList = new ArrayList<>();
	    String sql = "SELECT p.Post_Num, p.Post_Title, u.Nickname AS nickname, p.Post_createDay, f.File_Path, " +
	                 "mp.My_postURL AS profileImageUrl, p.Post_hits, p.Post_Heart, p.Post_Content " +
	                 "FROM Post p " +
	                 "LEFT JOIN File f ON p.Post_Num = f.Post_Num " +
	                 "LEFT JOIN User u ON p.User_Id = u.User_Id " +
	                 "LEFT JOIN MyProfile mp ON u.User_Id = mp.User_Id";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            int postId = rs.getInt("Post_Num");
	            String title = rs.getString("Post_Title");
	            String nickname = rs.getString("nickname"); // 닉네임 추가
	            String createDate = rs.getString("Post_createDay");
	            String postImageUrl = rs.getString("File_Path");
	            String profileImageUrl = rs.getString("profileImageUrl");
	            int views = rs.getInt("Post_hits");
	            int likes = rs.getInt("Post_Heart");
	            String content = rs.getString("Post_Content");

	            PostItem post = new PostItem(postId, title, nickname, createDate, content, postImageUrl, profileImageUrl, views, likes, nickname);
	            postList.add(post);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return postList;
	}

	
	
	public List<PostItem> getAllPostsSortedByDate() {
	    List<PostItem> postList = new ArrayList<>();
	    String sql = "SELECT p.Post_Num, p.Post_Title, u.Nickname AS nickname, p.Post_createDay, f.File_Path, " +
	                 "mp.My_postURL AS profileImageUrl, p.Post_hits AS views, p.Post_Heart AS likes, p.Post_Content " +
	                 "FROM Post p " +
	                 "LEFT JOIN File f ON p.Post_Num = f.Post_Num " +
	                 "LEFT JOIN User u ON p.User_Id = u.User_Id " +
	                 "LEFT JOIN MyProfile mp ON u.User_Id = mp.User_Id " +
	                 "ORDER BY p.Post_createDay DESC";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            int postId = rs.getInt("Post_Num");
	            String title = rs.getString("Post_Title");
	            String nickname = rs.getString("nickname");
	            String createDate = rs.getString("Post_createDay");
	            String postImageUrl = rs.getString("File_Path");
	            String profileImageUrl = rs.getString("profileImageUrl");
	            int views = rs.getInt("views");
	            int likes = rs.getInt("likes");
	            String content = rs.getString("Post_Content");

	            PostItem post = new PostItem(postId, title, nickname, createDate, content, postImageUrl, profileImageUrl, views, likes, nickname);
	            postList.add(post);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return postList;
	}

	

	public PostItem getPostDetails(String postId) {
	    String sql = "SELECT p.Post_Num, p.Post_Title, u.Nickname AS nickname, p.Post_Content, p.Post_createDay, " +
	                 "f.File_Path AS attachmentUrl, mp.My_postURL AS profileImageUrl, " +
	                 "p.Post_hits, p.Post_Heart " +
	                 "FROM Post p " +
	                 "LEFT JOIN File f ON p.Post_Num = f.Post_Num " +
	                 "LEFT JOIN User u ON p.User_Id = u.User_Id " +
	                 "LEFT JOIN MyProfile mp ON u.User_Id = mp.User_Id " +
	                 "WHERE p.Post_Num = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, postId);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            int postIdInt = rs.getInt("Post_Num");
	            String title = rs.getString("Post_Title");
	            String nickname = rs.getString("nickname"); // 닉네임 추가
	            String content = rs.getString("Post_Content");
	            String createDate = rs.getString("Post_createDay");
	            String attachmentUrl = rs.getString("attachmentUrl");
	            String profileImageUrl = rs.getString("profileImageUrl");
	            int views = rs.getInt("Post_hits");
	            int likes = rs.getInt("Post_Heart");

	            return new PostItem(postIdInt, title, nickname, createDate, content, attachmentUrl, profileImageUrl, views, likes, nickname);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	public List<PostItem> getPostsByCategory(String categoryCode) {
	    List<PostItem> postList = new ArrayList<>();
	    String sql = "SELECT p.Post_Num, p.Post_Title, u.Nickname AS nickname, p.Post_createDay, f.File_Path AS postImageUrl, " +
	                 "mp.My_postURL AS profileImageUrl, p.Post_hits, p.Post_Heart, p.Post_Content " +
	                 "FROM Post p " +
	                 "LEFT JOIN File f ON p.Post_Num = f.Post_Num " +
	                 "LEFT JOIN User u ON p.User_Id = u.User_Id " +
	                 "LEFT JOIN MyProfile mp ON u.User_Id = mp.User_Id " +
	                 "WHERE p.Category_Code = ? " +
	                 "ORDER BY p.Post_createDay DESC";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, categoryCode);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            int postId = rs.getInt("Post_Num");
	            String title = rs.getString("Post_Title");
	            String nickname = rs.getString("nickname");
	            String createDate = rs.getString("Post_createDay");
	            String postImageUrl = rs.getString("postImageUrl");
	            String profileImageUrl = rs.getString("profileImageUrl");
	            int views = rs.getInt("Post_hits");
	            int likes = rs.getInt("Post_Heart");
	            String content = rs.getString("Post_Content");

	            PostItem post = new PostItem(postId, title, nickname, createDate, content, postImageUrl, profileImageUrl, views, likes, nickname);
	            postList.add(post);
	        }

	        System.out.println("getPostsByCategory found: " + postList.size() + " posts for categoryCode: " + categoryCode);

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return postList;
	}

}
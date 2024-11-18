package kch_DAO;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kch_java.Comment;
import kch_java.PostItem;
import kch_java.User;

public class DAO_User extends BaseDAO {

	//객체 생성시, 생성자에서 자동으로 TBG데이터베이스에 커넥션된다.
	public DAO_User() throws SQLException, ClassNotFoundException {
        super();
    }
	
	// 사용자와 게시물별 좋아요 기록을 저장할 맵 (static으로 변경하여 모든 인스턴스가 공유)
	private static Map<String, LocalDate> likeRecords = new HashMap<>();
    private Map<String, LocalDate> viewRecords = new HashMap<>();
	
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
	    String sql = "SELECT p.Post_Num, p.Post_Title, u.Nickname AS nickname, p.Post_createDay, f.File_Path, f.File_extension, " +
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
	            String nickname = rs.getString("nickname");
	            String createDate = rs.getString("Post_createDay");
	            String filePath = rs.getString("File_Path");
	            String fileExtension = rs.getString("File_extension");
	            String profileImageUrl = rs.getString("profileImageUrl");
	            int views = rs.getInt("Post_hits");
	            int likes = rs.getInt("Post_Heart");
	            String content = rs.getString("Post_Content");

	            // 파일 확장자에 따라 이미지 또는 동영상 URL 설정
	            String postImageUrl = null;
	            String postVideoUrl = null;
	            if (fileExtension != null) {
	                if (fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("png")) {
	                    postImageUrl = filePath;
	                } else if (fileExtension.equalsIgnoreCase("mp4") || fileExtension.equalsIgnoreCase("avi")) {
	                    postVideoUrl = filePath;
	                }
	            }

	            PostItem post = new PostItem(postId, title, nickname, createDate, content, postImageUrl, postVideoUrl, profileImageUrl, views, likes, nickname);
	            postList.add(post);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return postList;
	}


	
	
	public List<PostItem> getAllPostsSortedByDate() {
	    List<PostItem> postList = new ArrayList<>();
	    String sql = "SELECT p.Post_Num, p.Post_Title, u.Nickname AS nickname, p.Post_createDay, f.File_Path AS filePath, f.File_extension, " +
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
	            String filePath = rs.getString("filePath");
	            String fileExtension = rs.getString("File_extension");
	            String profileImageUrl = rs.getString("profileImageUrl");
	            int views = rs.getInt("views");
	            int likes = rs.getInt("likes");
	            String content = rs.getString("Post_Content");

	            // 파일 확장자에 따라 이미지 또는 동영상 URL 설정
	            String postImageUrl = null;
	            String postVideoUrl = null;
	            if (fileExtension != null) {
	                if (fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("png")) {
	                    postImageUrl = filePath;
	                } else if (fileExtension.equalsIgnoreCase("mp4") || fileExtension.equalsIgnoreCase("avi")) {
	                    postVideoUrl = filePath;
	                }
	            }

	            PostItem post = new PostItem(postId, title, nickname, createDate, content, postImageUrl, postVideoUrl, profileImageUrl, views, likes, nickname);
	            postList.add(post);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return postList;
	}
	

	public PostItem getPostDetails(String postId) {
	    String sql = "SELECT p.Post_Num, p.Post_Title, u.Nickname AS nickname, p.Post_Content, p.Post_createDay, " +
	                 "f.File_Path AS attachmentUrl, f.File_extension, mp.My_postURL AS profileImageUrl, " +
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
	            String nickname = rs.getString("nickname");
	            String content = rs.getString("Post_Content");
	            String createDate = rs.getString("Post_createDay");
	            String attachmentUrl = rs.getString("attachmentUrl");
	            String fileExtension = rs.getString("File_extension");
	            String profileImageUrl = rs.getString("profileImageUrl");
	            int views = rs.getInt("Post_hits");
	            int likes = rs.getInt("Post_Heart");

	            // 파일 확장자에 따라 이미지 또는 동영상 URL 설정
	            String postImageUrl = null;
	            String postVideoUrl = null;
	            if (fileExtension != null) {
	                if (fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("png")) {
	                    postImageUrl = attachmentUrl;
	                } else if (fileExtension.equalsIgnoreCase("mp4") || fileExtension.equalsIgnoreCase("avi")) {
	                    postVideoUrl = attachmentUrl;
	                }
	            }

	            return new PostItem(postIdInt, title, nickname, createDate, content, postImageUrl, postVideoUrl, profileImageUrl, views, likes, nickname);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	
	public List<PostItem> getPostsByCategory(String categoryCode) {
	    List<PostItem> postList = new ArrayList<>();
	    String sql = "SELECT p.Post_Num, p.Post_Title, u.Nickname AS nickname, p.Post_createDay, f.File_Path AS filePath, f.File_extension, " +
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
	            String filePath = rs.getString("filePath");
	            String fileExtension = rs.getString("File_extension");
	            String profileImageUrl = rs.getString("profileImageUrl");
	            int views = rs.getInt("Post_hits");
	            int likes = rs.getInt("Post_Heart");
	            String content = rs.getString("Post_Content");

	            // 파일 확장자에 따라 이미지 또는 동영상 URL 설정
	            String postImageUrl = null;
	            String postVideoUrl = null;
	            if (fileExtension != null) {
	                if (fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("png")) {
	                    postImageUrl = filePath;
	                } else if (fileExtension.equalsIgnoreCase("mp4") || fileExtension.equalsIgnoreCase("avi")) {
	                    postVideoUrl = filePath;
	                }
	            }

	            PostItem post = new PostItem(postId, title, nickname, createDate, content, postImageUrl, postVideoUrl, profileImageUrl, views, likes, nickname);
	            postList.add(post);
	        }

	        System.out.println("getPostsByCategory found: " + postList.size() + " posts for categoryCode: " + categoryCode);

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return postList;
	}
	
	public List<Comment> getCommentsByPostId(String postId) {
	    List<Comment> commentList = new ArrayList<>();
	    String sql = "SELECT c.Comment_Id, c.Comment_Contents, c.Comment_Date, u.User_Id AS authorId, u.Nickname, mp.My_postURL AS profileImageUrl " +
	                 "FROM Comment c " +
	                 "JOIN User u ON c.User_Id = u.User_Id " +
	                 "LEFT JOIN MyProfile mp ON u.User_Id = mp.User_Id " +
	                 "WHERE c.Post_Num = ? " +
	                 "ORDER BY c.Comment_Date ASC";  // Comment_Date를 기준으로 오름차순 정렬

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, postId);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            String author = rs.getString("Nickname");
	            String content = rs.getString("Comment_Contents");
	            String date = rs.getString("Comment_Date");
	            String profileImageUrl = rs.getString("profileImageUrl");
	            String authorId = rs.getString("authorId");
	            String commentId = rs.getString("Comment_Id");

	            commentList.add(new Comment(author, content, date, profileImageUrl, authorId, commentId));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return commentList;
	}




	
	public String saveComment(String postId, String userId, String content) {
	    String sql = "INSERT INTO Comment (Comment_Date, Comment_Contents, Post_Num, User_Id) VALUES (NOW(), ?, ?, ?)";
	    ResultSet rs = null;

	    try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, content);
	        pstmt.setString(2, postId);
	        pstmt.setString(3, userId);
	        
	        int affectedRows = pstmt.executeUpdate();
	        
	        if (affectedRows > 0) {
	            // 생성된 Comment_Id 가져오기
	            rs = pstmt.getGeneratedKeys();
	            if (rs.next()) {
	                return String.valueOf(rs.getInt(1)); // Comment_Id를 문자열 형태로 반환
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (rs != null) rs.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    return null; // 실패 시 null 반환
	}





	
	
	 // 하루에 한 번만 좋아요 가능
    public boolean incrementLikeCount(int postNum, String userId) {
        if (!canUserLikeToday(userId, postNum)) {
            return false; // 이미 오늘 좋아요를 눌렀다면 증가하지 않음
        }

        String likeSql = "UPDATE Post SET Post_Heart = Post_Heart + 1 WHERE Post_Num = ?";
        try (PreparedStatement likeStmt = conn.prepareStatement(likeSql)) {
            likeStmt.setInt(1, postNum);
            if (likeStmt.executeUpdate() > 0) {
                recordLike(userId, postNum); // 사용자가 오늘 좋아요를 누른 기록을 갱신
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

	

    // 하루에 한 번 조회수 증가 메서드
    public boolean incrementViewCount(int postNum) {
        String sql = "UPDATE Post SET Post_hits = Post_hits + 1 WHERE Post_Num = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postNum);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




    public boolean canUserLikeToday(String userId, int postNum) {
        String key = userId + "_" + postNum;
        LocalDate lastLikedDate = likeRecords.get(key);
        LocalDate today = LocalDate.now();

        // 오늘 이미 좋아요를 눌렀다면 false 반환
        if (today.equals(lastLikedDate)) {
            System.out.println("오늘 이미 좋아요를 눌렀습니다. key: " + key);
            return false;
        }
        System.out.println("좋아요 가능. key: " + key);
        return true;
    }
    
    public void recordLike(String userId, int postNum) {
        String key = userId + "_" + postNum;
        likeRecords.put(key, LocalDate.now());
        System.out.println("좋아요 기록 업데이트 완료: " + key);
    }
    
    public boolean updatePost(int postNum, String userId, String title, String content, String categoryCode) {
        String sql = "UPDATE Post SET Post_Title = ?, Post_Content = ?, Category_Code = ? WHERE Post_Num = ? AND User_Id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setString(3, categoryCode);
            pstmt.setInt(4, postNum);
            pstmt.setString(5, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFile(int postNum, String filePath, String fileName, String fileExtension, String fileCapacity) {
        String deleteOldFileSql = "DELETE FROM File WHERE Post_Num = ?";
        String insertNewFileSql = "INSERT INTO File (Post_Num, File_Path, File_name, File_extension, File_capacity) VALUES (?, ?, ?, ?, ?)";
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteOldFileSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertNewFileSql)) {

                // 기존 파일 삭제
                deleteStmt.setInt(1, postNum);
                deleteStmt.executeUpdate();

                // 새 파일 정보 삽입
                insertStmt.setInt(1, postNum);
                insertStmt.setString(2, filePath);
                insertStmt.setString(3, fileName);
                insertStmt.setString(4, fileExtension);  // 실제 파일 확장자 저장
                insertStmt.setString(5, fileCapacity);
                insertStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean deletePost(int postNum, String userId) {
        String sql = "DELETE FROM Post WHERE Post_Num = ? AND User_Id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postNum);
            pstmt.setString(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // 삭제된 행이 1개 이상이면 성공
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 오류 발생 시 false 반환
        }
    }
    public boolean deleteComment(String commentId) {
        String sql = "DELETE FROM Comment WHERE Comment_Id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, commentId);
            int deletedRows = pstmt.executeUpdate();
            return deletedRows > 0; // 삭제 성공 여부 반환
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean addFavorite(String userId, int postNum) {
        String checkSql = "SELECT 1 FROM Favorites WHERE User_Id = ? AND Post_Num = ?";
        String insertSql = "INSERT INTO Favorites (User_Id, Post_Num) VALUES (?, ?)";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            // 중복 확인
            checkStmt.setString(1, userId);
            checkStmt.setInt(2, postNum);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                // 이미 즐겨찾기에 추가된 경우
                System.out.println("즐겨찾기에 이미 존재합니다: User_Id=" + userId + ", Post_Num=" + postNum);
                return false; // 중복된 경우 false 반환
            }

            // 중복되지 않은 경우 삽입
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, userId);
                insertStmt.setInt(2, postNum);
                return insertStmt.executeUpdate() > 0; // 삽입 성공 여부 반환
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean removeFavorite(String userId, int postNum) {
        String checkSql = "SELECT 1 FROM Favorites WHERE User_Id = ? AND Post_Num = ?";
        String deleteSql = "DELETE FROM Favorites WHERE User_Id = ? AND Post_Num = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, userId);
            checkStmt.setInt(2, postNum);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                // 삭제할 항목이 없을 경우
                System.out.println("삭제 대상이 존재하지 않습니다: User_Id=" + userId + ", Post_Num=" + postNum);
                return false;
            }

            // 삭제 수행
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, userId);
                deleteStmt.setInt(2, postNum);
                return deleteStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Integer> getUserFavorites(String userId) {
        List<Integer> favoritePosts = new ArrayList<>();
        String sql = "SELECT Post_Num FROM Favorites WHERE User_Id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                favoritePosts.add(rs.getInt("Post_Num"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favoritePosts;
    }
    
    public boolean isFavorite(String userId, int postNum) {
        String sql = "SELECT 1 FROM Favorites WHERE User_Id = ? AND Post_Num = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setInt(2, postNum);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // 결과가 존재하면 true 반환
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 오류 발생 시 false 반환
        }
    }

    public int getFavoriteCount(String userId) throws SQLException {
        if (!isUserExists(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
        }

        String sql = "SELECT COUNT(*) FROM Favorites WHERE User_Id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // 즐겨찾기 개수 반환
            }
        }
        return 0; // 기본값 반환
    }
    
    public int getPostCount(String userId) throws SQLException {
        if (!isUserExists(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
        }

        String sql = "SELECT COUNT(*) FROM Post WHERE User_Id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // 게시글 개수 반환
            }
        }
        return 0; // 기본값 반환
    }

    public boolean isUserExists(String userId) throws SQLException {
        String sql = "SELECT 1 FROM User WHERE User_Id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // 결과가 있으면 true 반환
        }
    }


    
}
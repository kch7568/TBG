package kch_server;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import kch_DAO.DAO_User;
import kch_java.PostItem;
import kch_java.Session;
import kch_java.SessionManager;

@WebServlet("/GetMyPostsServlet")
public class GetMyPostsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public GetMyPostsServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");

        String sessionId = request.getHeader("sessionId");
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.getSession(sessionId);

        // 세션 검증
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid or expired session.\"}");
            return;
        }

        String userId = session.getUserId();

        DAO_User dao = null; // DAO_User 객체를 수동으로 관리
        try {
            dao = new DAO_User(); // DAO_User 객체 생성
            List<PostItem> myPosts = dao.getAllPostsByUser(userId);

            JSONArray postsArray = new JSONArray();
            for (PostItem post : myPosts) {
                System.out.println("포스트아이디 : " + post.getPostNum() );
                System.out.println("포스트제목 : " + post.getTitle() );
                System.out.println("포스트이미지 URL: " + post.getPostImageUrl());
                System.out.println("포스트동영상 URL: " + post.getPostVideoUrl());
                System.out.println("프로필이미지 URL: " + post.getProfileImageUrl());
                System.out.println("포스트내용 : " + post.getContent() );
                System.out.println("포스트날짜 : " + post.getCreateDate() );
                System.out.println("포스트조회수 : " + post.getViews());
                System.out.println("포스트좋아요 : " + post.getLikes() );
                System.out.println("포스트닉넴 : " + post.getNickname() );
                System.out.println("포스트작성자 : " + post.getNickname() );
                JSONObject postJson = new JSONObject();
         
                postJson.put("postId", post.getPostNum());
                postJson.put("title", post.getTitle());
                postJson.put("content", post.getContent());
                postJson.put("createDate", post.getCreateDate());
                postJson.put("views", post.getViews());
                postJson.put("likes", post.getLikes());
                postJson.put("nickname", post.getNickname()); // nickname 추가
                postJson.put("author", post.getNickname()); // author 추가 (닉네임과 동일하다면)
                postJson.put("postImageUrl", post.getPostImageUrl()); 
                postJson.put("postVideoUrl", post.getPostVideoUrl()); 
                postJson.put("profileImageUrl", post.getProfileImageUrl()); 
                postsArray.put(postJson);
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(postsArray.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Error retrieving posts: " + e.getMessage() + "\"}");
            e.printStackTrace(); // 서버 로그에 에러 출력
        } finally {
            if (dao != null) { // DAO_User 객체가 생성되었으면
                try {
                    dao.close(); // 명시적으로 리소스 해제
                } catch (Exception e) {
                    e.printStackTrace(); // close 중 발생한 에러 출력
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}

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
import kch_java.Comment;
import kch_java.Session;
import kch_java.SessionManager;

@WebServlet("/GetMyCommentsServlet")
public class GetMyCommentsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public GetMyCommentsServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");

        // 세션 검증
        String sessionId = request.getHeader("sessionId");
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.getSession(sessionId);

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid or expired session.\"}");
            return;
        }

        String userId = session.getUserId();

        DAO_User dao = null; // DAO_User 객체 수동 관리
        try {
            dao = new DAO_User(); // DAO_User 인스턴스 생성
            List<Comment> myComments = dao.getAllCommentsByUserWithPostTitle(userId);

            JSONArray commentsArray = new JSONArray();
            for (Comment comment : myComments) {
                JSONObject commentJson = new JSONObject();
                commentJson.put("commentId", comment.getCommentId());         // 댓글 ID
                commentJson.put("postId", comment.getAuthorId());            // 게시글 ID
                commentJson.put("content", comment.getContent());            // 댓글 내용
                commentJson.put("createDate", comment.getDate());            // 작성일자
                commentJson.put("relatedPostTitle", comment.getAuthor());    // 게시글 제목
                commentsArray.put(commentJson);
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(commentsArray.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Error retrieving comments");
            errorResponse.put("message", e.getMessage());
            response.getWriter().write(errorResponse.toString());
            e.printStackTrace();
        } finally {
            if (dao != null) {
                try {
                    dao.close(); // DAO_User의 close() 메서드 호출
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}

package kch_server;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import kch_DAO.DAO_User;
import kch_java.Comment;
import kch_java.Session;
import kch_java.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/GetComments")
public class CommentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String postId = request.getParameter("postId");
        String sessionId = request.getParameter("sessionId"); // 세션 ID 가져오기
        DAO_User daoUser;

        try {
            daoUser = new DAO_User();

            // 세션을 통해 사용자 닉네임 가져오기
            String nickname = "익명"; // 기본 닉네임
            if (sessionId != null) {
                SessionManager sessionManager = SessionManager.getInstance();
                Session session = sessionManager.getSession(sessionId);
                if (session != null) {
                    nickname = session.getNickname(); // 세션에서 닉네임 가져오기
                }
            }

            List<Comment> comments = daoUser.getCommentsByPostId(postId);
            JSONArray commentArray = new JSONArray();
            for (Comment comment : comments) {
                JSONObject commentObject = new JSONObject();
                commentObject.put("author", comment.getAuthor() != null ? comment.getAuthor() : nickname);
                commentObject.put("content", comment.getContent());
                commentObject.put("date", comment.getDate());
                commentObject.put("profileImageUrl", comment.getProfileImageUrl());
                commentArray.put(commentObject);
            }
            out.print(commentArray.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Class not found error occurred.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"댓글을 불러오는 중 오류가 발생했습니다.\"}");
        } finally {
            out.close();
        }
    }
}

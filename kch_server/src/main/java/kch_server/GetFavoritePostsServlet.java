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
import kch_java.PostItem;
import kch_java.Session;
import kch_java.SessionManager;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/GetFavoritePosts")
public class GetFavoritePostsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        DAO_User daoUser = null;

        try {
            String sessionId = request.getParameter("sessionId");
            if (sessionId == null || sessionId.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Session ID is required\"}");
                return;
            }

            // 세션 관리
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.getSession(sessionId);
            if (session == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\": \"Invalid session ID\"}");
                return;
            }

            String userId = session.getUserId();

            // DAO 호출
            daoUser = new DAO_User();
            List<PostItem> favoritePosts = daoUser.getFavoritePosts(userId);

            // JSON 변환
            JSONArray postArray = new JSONArray();
            for (PostItem post : favoritePosts) {
                JSONObject postObject = new JSONObject();
                postObject.put("postNum", post.getPostNum());
                postObject.put("title", post.getTitle());
                postObject.put("nickname", post.getNickname());
                postObject.put("date", post.getCreateDate());
                postObject.put("profileImageUrl", post.getProfileImageUrl() != null ? post.getProfileImageUrl() : "http://43.201.243.50:8080/kch_server/images/default_profile.jpg");
                postObject.put("content", post.getContent());
                postObject.put("postImageUrl", post.getPostImageUrl() != null ? post.getPostImageUrl() : "");
                postObject.put("postVideoUrl", post.getPostVideoUrl() != null ? post.getPostVideoUrl() : "");
                postObject.put("views", post.getViews());
                postObject.put("likes", post.getLikes());
                postArray.put(postObject);
            }

            out.print(postArray.toString());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"An error occurred while fetching favorite posts\"}");
        } finally {
            if (daoUser != null) {
                try {
                    daoUser.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

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
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/GetPosts")
public class GetPostsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        DAO_User daoUser = null;

        try {
            daoUser = new DAO_User();

            // 카테고리 코드 파라미터를 받아옴
            String categoryCode = request.getParameter("categoryCode");
            List<PostItem> postList;

            if (categoryCode != null && !categoryCode.equals("전체")) {
                // 특정 카테고리 코드에 맞는 게시물 가져오기
                postList = daoUser.getPostsByCategory(categoryCode);
            } else {
                // 모든 게시물 가져오기
                postList = daoUser.getAllPostsSortedByDate();
            }

            JSONArray postArray = new JSONArray();
            for (PostItem post : postList) {
                JSONObject postObject = new JSONObject();
                postObject.put("postId", post.getPostId());
                postObject.put("title", post.getTitle());
                postObject.put("nickname", post.getNickname());
                postObject.put("date", post.getCreateDate());
                postObject.put("profileImageUrl", post.getProfileImageUrl() != null ? post.getProfileImageUrl() : "http://localhost:8888/kch_server/images/default_profile.jpg");
                postObject.put("postImageUrl", post.getPostImageUrl() != null ? post.getPostImageUrl() : "");
                postObject.put("views", post.getViews());
                postObject.put("likes", post.getLikes());

                postArray.put(postObject);
            }

            out.print(postArray.toString());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"An error occurred while fetching posts\"}");
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

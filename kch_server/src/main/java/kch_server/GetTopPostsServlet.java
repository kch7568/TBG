package kch_server;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kch_DAO.DAO_User;
import kch_java.PostItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/GetTopPosts")
public class GetTopPostsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        DAO_User daoUser = null;

        try {
            daoUser = new DAO_User();
            int limit = 2; // 상위 2개의 게시글만 가져오기
            List<PostItem> topPosts = daoUser.getTopPostsByLikes(limit);

            JSONArray jsonArray = new JSONArray();
            for (PostItem post : topPosts) {
                // 디버깅 로그 추가
                System.out.println("Post_Num: " + post.getPostNum());
                System.out.println("Post_Content: " + post.getTitle());
                System.out.println("Post_Title: " + post.getContent());
                System.out.println("Post_ImageUrl: " + post.getPostImageUrl()); // 확인
                System.out.println("Post_VideoUrl: " + post.getPostVideoUrl()); // 확인
                System.out.println("Post_Nickname: " +   post.getNickname()); // 확인
                System.out.println("Post_date: " + post.getCreateDate()); // 확인
                System.out.println("Post_Views: " + post.getViews()); // 확인
                System.out.println("Post_Likes: " + post.getLikes()); // 확인
                
                JSONObject postJson = new JSONObject();
                postJson.put("postNum", post.getPostNum());
                postJson.put("title", post.getTitle());
                postJson.put("content", post.getContent());
                postJson.put("nickname", post.getNickname());
                postJson.put("date", post.getCreateDate());
                postJson.put("views", post.getViews());
                postJson.put("likes", post.getLikes());
                postJson.put("profileImageUrl", 
                    post.getProfileImageUrl() != null 
                        ? post.getProfileImageUrl() 
                        : "http://43.201.243.50:8080/kch_server/images/default_profile.jpg");
                postJson.put("postImageUrl", post.getPostImageUrl()); // 대표 이미지 URL 추가
                postJson.put("postVideoUrl", post.getPostVideoUrl()); // 동영상 URL 추가
                jsonArray.put(postJson);
                System.out.println("Post_VideoUrl재확인: " + post.getPostVideoUrl()); // 확인
            }

            out.print(jsonArray.toString());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"An error occurred while fetching top posts\"}");
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

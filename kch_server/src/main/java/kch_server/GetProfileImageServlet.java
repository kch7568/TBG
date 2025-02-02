package kch_server;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;
import kch_DAO.DAO_User;
import kch_java.Session;
import kch_java.SessionManager;

@WebServlet("/GetProfileImage")
public class GetProfileImageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");

        PrintWriter out = response.getWriter();

        // 클라이언트가 보낸 sessionId 파라미터로 세션 조회
        String sessionId = request.getParameter("sessionId");
        System.out.println("Received sessionId: " + sessionId);  // 디버그 로그

        // 세션 매니저에서 세션을 찾음
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.getSession(sessionId);

        JSONObject jsonResponse = new JSONObject();

        // 세션이 유효한지 확인
        if (session == null) {
            System.out.println("Session not found or invalid for sessionId: " + sessionId);  // 디버그 로그
            jsonResponse.put("status", "fail");
            jsonResponse.put("message", "Invalid session ID.");
            out.print(jsonResponse.toString());
            return;
        }

        String userId = session.getUserId();  // 세션에서 userId를 가져옴
        System.out.println("Session is valid. userId: " + userId);  // 디버그 로그

        try {
            DAO_User userDao = new DAO_User();
            String imageUrl = userDao.getProfileImageUrl(userId);
            System.out.println("Retrieved imageUrl: " + imageUrl);  // 디버그 로그

            jsonResponse.put("status", "success");
            jsonResponse.put("imageUrl", imageUrl != null ? imageUrl : "http://43.201.243.50:8080/kch_server/images/default_profile.jpg");
            out.print(jsonResponse.toString());

            System.out.println("Response sent: " + jsonResponse.toString());  // 디버그 로그

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "fail");
            jsonResponse.put("message", "Failed to retrieve image.");
            out.print(jsonResponse.toString());

            System.out.println("Exception occurred: " + e.getMessage());  // 디버그 로그
        }
    }
}
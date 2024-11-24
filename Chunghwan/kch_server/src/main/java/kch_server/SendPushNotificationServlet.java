package kch_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import kch_DAO.DAO_User;
import kch_java.FcmNotificationSender;
import kch_java.Session;
import kch_java.SessionManager;

@WebServlet("/SendPushNotification")
public class SendPushNotificationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            // 요청 데이터 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            JSONObject requestJson = new JSONObject(requestBody.toString());

            String postNum = requestJson.getString("postNum");
            String commentContent = requestJson.getString("commentContent");
            String sessionId = requestJson.getString("sessionId");     //검증추가
            
            String userId = null;
            if (sessionId != null) {
                SessionManager sessionManager = SessionManager.getInstance();
                Session session = sessionManager.getSession(sessionId);
                if (session != null) {
                    userId = session.getUserId();  //현재 유저 아이디
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write("{\"error\": \"Invalid session ID.\"}");
                    System.out.println("요청 실패: 잘못된 session ID.");
                    return;
                }
            } else {
                System.out.println("요청 실패: session ID 누락.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\": \"Missing session ID.\"}");
                return;
            }

            
            // 게시글 작성자의 FCM 토큰 가져오기
            DAO_User daoUser = new DAO_User();
            String authorId = daoUser.getUserByPostId(postNum); // 게시글 작성자 ID
            String fcmToken = daoUser.getFcmToken(authorId);

            
            if(!userId.equals(authorId))  ////////////////////////////////// 여기에서  나랑 게시글작성자랑 맞는지 검증
            if (fcmToken != null && !fcmToken.isEmpty()) {
                // FCM 푸시 알림 전송
                FcmNotificationSender.sendNotification(
                    fcmToken,
                    "새 댓글 알림",
                    "새로운 댓글: " + commentContent
                );
                jsonResponse.put("success", true);
                jsonResponse.put("message", "푸시 알림이 전송되었습니다.");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "FCM 토큰이 존재하지 않습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("success", false);
            jsonResponse.put("message", "오류 발생: " + e.getMessage());
        }

        out.print(jsonResponse.toString());
    }
}

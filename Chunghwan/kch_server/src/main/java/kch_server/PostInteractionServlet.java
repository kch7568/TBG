package kch_server;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import kch_DAO.DAO_User;
import kch_java.Session;
import kch_java.SessionManager;

@WebServlet("/PostInteraction")
public class PostInteractionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        String postNumParam = request.getParameter("postNum");
        String sessionId = request.getParameter("sessionId");
        
        System.out.println("Received postNum: " + postNumParam);

        if (postNumParam == null || action == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Missing required parameters.\"}");
            System.out.println("요청 실패: 파라미터 누락.");
            return;
        }

        int postNum;
        try {
            postNum = Integer.parseInt(postNumParam);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Invalid postNum format.\"}");
            System.out.println("요청 실패: 잘못된 postNum 형식.");
            return;
        }

        String userId = null;
        if (sessionId != null) {
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.getSession(sessionId);
            if (session != null) {
                userId = session.getUserId();
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

        DAO_User daoUser = null;
        boolean success = false;
        String message = "";  // 좋아요 성공 여부 메시지

        try {
            daoUser = new DAO_User();

            if ("incrementView".equals(action)) {
                success = daoUser.incrementViewCount(postNum);
                System.out.println("조회수 증가 호출 - 성공 여부: " + success);
                message = "조회수 증가 성공";
            } 
         // PostInteractionServlet.java

            else if ("likePost".equals(action) && userId != null) {
                if (!daoUser.canUserLikeToday(userId, postNum)) {  // 오늘 좋아요를 이미 눌렀는지 확인
                    success = false;
                    message = "오늘은 더 이상 이 게시물을 좋아요 할 수 없습니다.";
                    System.out.println("좋아요 제한: " + message);
                } else {
                    success = daoUser.incrementLikeCount(postNum, userId);
                    if (success) {
                        daoUser.recordLike(userId, postNum);  // 좋아요 기록 업데이트
                        message = "좋아요 수가 올라갔습니다!";
                    } else {
                        message = "좋아요 실패.";
                    }
                    System.out.println("좋아요 증가 호출 - 성공 여부: " + success);
                }
            }

            	else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\": \"Invalid action or missing session ID for likePost.\"}");
                return;
            }

         // 결과와 메시지 반환
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", success);
            jsonResponse.put("message", message);
            out.write(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"An error occurred.\"}");
        } finally {
            if (daoUser != null) {
                try {
                    daoUser.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

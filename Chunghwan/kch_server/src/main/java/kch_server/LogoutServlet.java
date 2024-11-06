package kch_server;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import kch_java.SessionManager;

@WebServlet("/Logout")
public class LogoutServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject jsonResponse = new JSONObject();

        try {
            String sessionId = request.getParameter("sessionId");

            if (sessionId == null || sessionId.isEmpty()) {
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "Session ID is required.");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            // 세션 ID로 세션 무효화
            SessionManager sessionManager = SessionManager.getInstance();
            if (sessionManager.getSession(sessionId) != null) {
                sessionManager.invalidateSession(sessionId);
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "성공적으로 로그아웃되었습니다.");
            } else {
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "유효하지 않은 세션 ID입니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "서버 오류가 발생했습니다.");
        }

        response.getWriter().write(jsonResponse.toString());
    }
}

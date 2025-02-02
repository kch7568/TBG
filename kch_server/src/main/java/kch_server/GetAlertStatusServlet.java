package kch_server;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import kch_DAO.DAO_User;
import kch_java.Session;
import kch_java.SessionManager;

@WebServlet("/GetAlertStatus")
public class GetAlertStatusServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject jsonResponse = new JSONObject();

        try {
            // 세션 ID 파라미터에서 가져오기
            String sessionId = request.getParameter("sessionId");

            if (sessionId == null || sessionId.isEmpty()) {
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "Session ID is required.");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            // 세션 ID로 실제 사용자 ID 찾기
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.getSession(sessionId);
            if (session == null) {
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "Invalid session ID.");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            String userId = session.getUserId();
            DAO_User userDao = new DAO_User();
            Boolean alertStatus = userDao.getAlertStatus(userId);

            if (alertStatus != null) {
                jsonResponse.put("status", "success");
                jsonResponse.put("alertStatus", alertStatus);
            } else {
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "Failed to retrieve alert status.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Server error occurred.");
        }

        response.getWriter().write(jsonResponse.toString());
    }
}
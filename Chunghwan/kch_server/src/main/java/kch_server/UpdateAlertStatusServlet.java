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


@WebServlet("/UpdateAlertStatus")
public class UpdateAlertStatusServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject jsonResponse = new JSONObject();

        try {
            String sessionId = request.getParameter("sessionId");
            String status = request.getParameter("status");

            // 세션 및 userId 검증
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.getSession(sessionId);

            if (session == null) {
                System.out.println("Invalid session ID: " + sessionId);
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "Invalid session ID.");
                response.getWriter().print(jsonResponse.toString());
                return;
            }

            String userId = session.getUserId();

            DAO_User userDao = new DAO_User();
            boolean updateSuccess = userDao.updateAlertStatus(userId, Boolean.parseBoolean(status));

            if (updateSuccess) {
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "알림 상태가 업데이트되었습니다.");
            } else {
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "알림 상태 업데이트에 실패했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "서버 오류가 발생했습니다.");
        }

        response.getWriter().write(jsonResponse.toString());
    }
}
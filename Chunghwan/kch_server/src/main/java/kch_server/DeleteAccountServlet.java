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

@WebServlet("/DeleteAccount")
public class DeleteAccountServlet extends HttpServlet {

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

            // 사용자 삭제 시 관련 데이터 삭제 로직 추가
            boolean deleteSuccess = userDao.deleteUser(userId);
            if (deleteSuccess) {
                // 세션 무효화
                sessionManager.invalidateSession(sessionId);
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "계정이 성공적으로 삭제되었습니다.");
            } else {
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "계정 삭제에 실패했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "서버 오류가 발생했습니다.");
        }

        response.getWriter().write(jsonResponse.toString());
    }
}

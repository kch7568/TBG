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
import kch_java.User;

@WebServlet("/PasswordReset")
public class PasswordResetServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject jsonResponse = new JSONObject();

        try {
            String sessionId = request.getParameter("sessionId");
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");

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
            User user = userDao.getUserById(userId);

            // 현재 비밀번호가 맞는지 확인
            if (user == null || !user.getPW().equals(currentPassword)) {
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "현재 비밀번호가 올바르지 않습니다.");
            } else if (!newPassword.equals(confirmPassword)) {
                // 새 비밀번호와 확인이 일치하는지 확인
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "새 비밀번호가 일치하지 않습니다.");
            } else {
                // 비밀번호 업데이트 시도
                user.setPW(newPassword);
                boolean updateSuccess = userDao.updatePassword(user);
                if (updateSuccess) {
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "비밀번호가 성공적으로 변경되었습니다.");
                } else {
                    jsonResponse.put("status", "fail");
                    jsonResponse.put("message", "비밀번호 변경에 실패했습니다. 다시 시도해주세요.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "서버 오류가 발생했습니다.");
        }

        // 응답 보내기
        response.getWriter().print(jsonResponse.toString());
    }
}

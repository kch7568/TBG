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

@WebServlet("/GetFavoriteStatusServlet")
public class GetFavoriteStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();

        DAO_User daoUser = null;

        try {
            // 요청에서 필요한 매개변수 가져오기
            String postNumStr = request.getParameter("postNum");
            String sessionId = request.getParameter("sessionId");

            // 세션 확인 및 사용자 ID 가져오기
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.getSession(sessionId);
            if (session == null) {
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "Invalid session ID.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().print(jsonResponse.toString());
                return;
            }
            String userId = session.getUserId();

            // 필수 매개변수 검증
            if (postNumStr == null || postNumStr.isEmpty()) {
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "Missing postNum parameter.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(jsonResponse.toString());
                return;
            }

            int postNum = Integer.parseInt(postNumStr);

            // DAO 초기화 및 즐겨찾기 상태 확인
            daoUser = new DAO_User();
            boolean isFavorite = daoUser.isFavorite(userId, postNum);

            // 응답 데이터 생성
            jsonResponse.put("status", "success");
            jsonResponse.put("isFavorite", isFavorite);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "An error occurred.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            // DAO 자원 해제
            if (daoUser != null) {
                try {
                    daoUser.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            response.getWriter().print(jsonResponse.toString());
        }
    }
}

package kch_server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kch_DAO.DAO_User;
import kch_java.Session;
import kch_java.SessionManager;
import org.json.JSONObject; // JSON 응답 생성을 위한 라이브러리 추가

@WebServlet("/GetAccountStatistics")
public class GetAccountStatisticsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String sessionId = request.getParameter("sessionId");

        // 세션 ID가 없으면 오류 반환
        if (sessionId == null || sessionId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Session ID is missing.");
            out.write(errorResponse.toString());
            return;
        }

        // SessionManager를 통해 세션 조회
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.getSession(sessionId);

        // 세션이 없거나 유효하지 않으면 오류 반환
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Invalid or expired session.");
            out.write(errorResponse.toString());
            return;
        }

        String userId = session.getUserId(); // 세션에서 userId 가져오기

        DAO_User dao = null;
        try {
            dao = new DAO_User(); // DAO 객체 생성

            // DAO를 사용하여 즐겨찾기와 게시글 수를 조회
            int favoriteCount = dao.getFavoriteCount(userId);
            int postCount = dao.getPostCount(userId);

            // JSON 응답 생성
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "success");
            jsonResponse.put("favoriteCount", favoriteCount);
            jsonResponse.put("postCount", postCount);

            out.write(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while processing the request.");
            out.write(errorResponse.toString());
        } finally {
            // 명시적으로 자원 해제
            if (dao != null) {
                try {
                    dao.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

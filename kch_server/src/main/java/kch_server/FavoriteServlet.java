package kch_server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import kch_DAO.DAO_User;
import kch_java.Session;
import kch_java.SessionManager;

@WebServlet("/FavoriteInteraction")
public class FavoriteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();
        PrintWriter out = response.getWriter();

        DAO_User daoUser = null;

        try {
            // 요청 매개변수 가져오기
            String action = request.getParameter("action");
            String postNumStr = request.getParameter("postNum");
            String sessionId = request.getParameter("sessionId");

            // 세션 유효성 검증
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.getSession(sessionId);
            if (session == null) {
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "Invalid session ID.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print(jsonResponse.toString());
                return;
            }

            String userId = session.getUserId();

            // 필수 매개변수 검증
            if (action == null || postNumStr == null) {
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "Missing action or postNum parameter.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(jsonResponse.toString());
                return;
            }

            // postNum 파싱
            int postNum = Integer.parseInt(postNumStr);

            // DAO 인스턴스 생성
            daoUser = new DAO_User();

            switch (action) {
                case "addFavorite":
                    if (daoUser.addFavorite(userId, postNum)) {
                        jsonResponse.put("status", "success");
                        jsonResponse.put("message", "Added to favorites.");
                    } else {
                        jsonResponse.put("status", "fail");
                        jsonResponse.put("message", "Already in favorites.");
                    }
                    break;

                case "removeFavorite":
                    if (daoUser.removeFavorite(userId, postNum)) {
                        jsonResponse.put("status", "success");
                        jsonResponse.put("message", "Removed from favorites.");
                    } else {
                        jsonResponse.put("status", "fail");
                        jsonResponse.put("message", "Not found in favorites.");
                    }
                    break;

                case "getFavoriteStatus":
                    boolean isFavorite = daoUser.isFavorite(userId, postNum);
                    jsonResponse.put("status", "success");
                    jsonResponse.put("isFavorite", isFavorite);
                    break;

                default:
                    jsonResponse.put("status", "fail");
                    jsonResponse.put("message", "Invalid action.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(jsonResponse.toString());
                    return;
            }

        } catch (NumberFormatException e) {
            jsonResponse.put("status", "fail");
            jsonResponse.put("message", "Invalid postNum format.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "An error occurred.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            if (daoUser != null) {
                try {
                    daoUser.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            out.print(jsonResponse.toString());
            out.close();
        }
    }
}

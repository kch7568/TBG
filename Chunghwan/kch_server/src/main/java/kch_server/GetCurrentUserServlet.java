package kch_server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import kch_java.Session;
import kch_java.SessionManager;

@WebServlet("/GetCurrentUser")
public class GetCurrentUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String sessionId = request.getParameter("sessionId");

            if (sessionId == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"fail\", \"message\":\"Missing sessionId\"}");
                return;
            }

            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.getSession(sessionId);

            if (session == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"status\":\"fail\", \"message\":\"Invalid sessionId\"}");
                return;
            }

            JSONObject userJson = new JSONObject();
            userJson.put("userId", session.getUserId());
            userJson.put("nickname", session.getNickname());

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(userJson.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"fail\", \"message\":\"An error occurred\"}");
        } finally {
            out.close();
        }
    }
}

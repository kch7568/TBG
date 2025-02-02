package kch_server;

import kch_java.Session;
import kch_java.SessionManager;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    public MainServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");

        // 클라이언트로부터 sessionId 받기
        String sessionId = request.getParameter("sessionId");

        // sessionId 확인
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.getSession(sessionId);

        if (session != null) {
            // 유효한 세션이면 닉네임 반환
            String userNickname = session.getNickname();  // 닉네임 가져오기
            response.getWriter().write("{\"status\": \"success\", \"nickname\": \"" + userNickname + "\"}");
        } else {
            // 유효하지 않은 세션이면 에러 메시지 반환
            response.getWriter().write("{\"status\": \"error\", \"message\": \"Invalid session.\"}");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}

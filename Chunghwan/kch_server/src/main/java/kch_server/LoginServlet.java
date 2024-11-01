package kch_server;

import kch_java.LoginLogout;
import kch_java.Session;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private LoginLogout loginLogout;

    @Override
    public void init() throws ServletException {
        try {
            loginLogout = new LoginLogout();  // LoginLogout 클래스 초기화
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Initialization error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 클라이언트에서 전송된 ID와 비밀번호를 받음
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");

        try {
        	response.setContentType("application/json; charset=UTF-8");
        	
            // 로그인 시도
            Session session = loginLogout.login(userId, password);
            if (session != null) {
            	response.getWriter().write("{\"message\": \"로그인에 성공하셨습니다!\", \"sessionId\": \"" + session.getSessionId() + "\"}");
            } else {
            	// 로그인 실패 시에도 JSON 형식으로 응답
                response.getWriter().write("{\"message\": \"로그인에 실패하셨습니다. 정보를 확인해주세요.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
         // 예외 발생 시에도 JSON 형식으로 응답
            response.getWriter().write("{\"message\": \"Error during login process.\"}");
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    }
}

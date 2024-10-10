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
            // 로그인 시도
            Session session = loginLogout.login(userId, password);
            if (session != null) {
                response.getWriter().write("Login successful! Session ID: " + session.getSessionId());
            } else {
                response.getWriter().write("Login failed. Invalid credentials.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("Error during login process.");
        }
    }
}

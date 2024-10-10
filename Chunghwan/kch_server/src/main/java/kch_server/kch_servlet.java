package kch_server;

import kch_java.Registration;
import kch_java.User;
import kch_java.LoginLogout;
import kch_DAO.DAO_User;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;


@WebServlet("/register")
public class kch_servlet extends HttpServlet {
    private Registration registration;

    @Override
    public void init() throws ServletException {
        try {
            registration = new Registration();  // Registration 클래스 초기화
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Initialization error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 클라이언트에서 전송된 사용자 정보를 받음
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");

        // 새로운 User 객체 생성
        User newUser = new User();
        newUser.setID(userId);
        newUser.setPW(password);
        newUser.setNickName(nickname);

     
            // 회원가입 시도
            if (registration.register(newUser)) {
                response.getWriter().write("Registration successful!");
            } else {
                response.getWriter().write("Registration failed. ID already exists.");
            }
       
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       
    }
}

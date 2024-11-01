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
    	 request.setCharacterEncoding("UTF-8");
        // 클라이언트에서 전송된 사용자 정보를 받음
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String password_re = request.getParameter("password_re");
        // 새로운 User 객체 생성
        User newUser = new User();
        newUser.setID(userId);
        newUser.setPW(password);
        newUser.setNickName(nickname);
        newUser.setPW_re(password_re);

     
        // 회원가입 시도
        if (registration.register(newUser)) {
            response.setContentType("text/plain; charset=UTF-8");
            HttpSession session = request.getSession();		//테스트
            session.setAttribute("id", newUser.getID());  //테스트
            response.getWriter().write("회원가입에 성공 하셨습니다!");
        } else {
            response.setContentType("text/plain; charset=UTF-8");

            if (newUser.getID().length() < 5 
                || newUser.getID().length() > 20 
                || newUser.getPW().length() < 5 
                || newUser.getPW().length() > 16) {      		          		
                response.getWriter().write("회원가입에 실패하셨습니다.\n 아이디와 비밀번호는 5자리부터 가능합니다.");   
                return;

            } else if (newUser.getNickName().length() < 3
                || newUser.getNickName().length() > 16) {
                response.getWriter().write("회원가입에 실패하셨습니다.\n 닉네임은 3~16글자여야 합니다.");            	
                return;

            } else if (!newUser.getID().matches("[a-zA-Z0-9]+")) {
                response.getWriter().write("회원가입에 실패하셨습니다.\n 아이디는 영어와 숫자로 작성되어야 합니다."); 
                return;

            } else if (!newUser.getPW().matches("[a-zA-Z0-9!@#$]+")) {
                response.getWriter().write("회원가입에 실패하셨습니다.\n 비밀번호는 영어와 숫자, !@#$로 작성되어야 합니다."); 
                return;

            } else if (!newUser.getPW().equals(newUser.getPW_re())) {  // 비밀번호와 확인란이 일치하는지 확인
                response.getWriter().write("회원가입에 실패하셨습니다.\n 비밀번호와 확인란이 동일하지 않습니다."); 
                return;

            } else if (!registration.checkNickName(newUser.getNickName())) {  // 닉네임 중복 확인
                response.getWriter().write("회원가입에 실패하셨습니다.\n 중복된 닉네임입니다."); 
                return;
            }
            
            else if (!newUser.getNickName().matches("^[a-zA-Z0-9가-힣]+$")) {
            	response.getWriter().write("회원가입에 실패하셨습니다.\n 닉네임은 영어,한글,숫자로 만들어야합니다.."); 
                return;
            }
            response.getWriter().write("회원가입에 실패하셨습니다.\n 중복된 아이디입니다."); 
        }
       
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       
    }
}

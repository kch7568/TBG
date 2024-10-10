package kch_java;

import kch_DAO.DAO_User;
import java.sql.SQLException;

public class LoginLogout {
    private DAO_User daoUser;
    private SessionManager sessionManager;

    public LoginLogout() throws SQLException, ClassNotFoundException {
        this.daoUser = new DAO_User();  // DAO 객체 생성
        this.sessionManager = new SessionManager();  // 세션 매니저 생성
    }

    // 로그인 처리
    public Session login(String userId, String password) throws SQLException {
        User user = daoUser.getUserById(userId);  // ID로 사용자 조회
        if (user != null && user.getPW().equals(password)) {  // 비밀번호 검증
            return sessionManager.createSession(userId);  // 세션 생성 후 반환
        }
        return null;  // 로그인 실패 시 null 반환
    }

    // 로그아웃 처리
    public void logout(String sessionId) {
        sessionManager.invalidateSession(sessionId);  // 세션 무효화
    }
}

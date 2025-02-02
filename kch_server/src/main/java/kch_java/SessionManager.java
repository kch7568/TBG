package kch_java;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static SessionManager instance;  // 싱글톤 인스턴스  --수정
    private Map<String, Session> sessions = new HashMap<>();  // 세션을 관리할 맵

    private SessionManager() {}  // private 생성자   --수정

    public static synchronized SessionManager getInstance() {  ///  --수정
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    } ///   --수정

    
    // 중복 세션 검사 및 기존 세션 만료
    private void invalidateExistingSession(String userId) {
        sessions.values().removeIf(session -> session.getUserId().equals(userId));
    }
    
    
    // 세션 생성
    public Session createSession(String userId, String nickname) {
    	  invalidateExistingSession(userId);  // 중복 로그인 방지: 기존 세션 만료
          String sessionId = generateSessionId();  // 새 세션 ID 생성
        Session session = new Session(sessionId, userId, nickname);
        sessions.put(sessionId, session);  // 세션 저장
        return session;
    }

    // 세션 찾기
    public Session getSession(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session != null && session.isValid()) {
            session.renew();  // 세션이 유효하면 만료 시간 연장
            return session;
        }
        return null;  // 세션이 없거나 만료된 경우
    }

    // 세션 무효화 (로그아웃)
    public void invalidateSession(String sessionId) {
        sessions.remove(sessionId);  // 세션 삭제
    }

    // 세션 ID 생성 (임의의 고유 ID 생성)
    private String generateSessionId() {
        return Long.toHexString(System.currentTimeMillis());
    }
}

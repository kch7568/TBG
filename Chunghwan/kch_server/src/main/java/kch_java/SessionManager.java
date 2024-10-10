package kch_java;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private Map<String, Session> sessions = new HashMap<>();  // 세션을 관리할 맵

    // 세션 생성
    public Session createSession(String userId) {
        String sessionId = generateSessionId();  // 세션 ID 생성
        Session session = new Session(sessionId, userId);
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

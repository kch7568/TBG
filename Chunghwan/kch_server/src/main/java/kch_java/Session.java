package kch_java;

import java.time.LocalDateTime;

public class Session {
    private String sessionId;  // 세션 ID
    private String userId;  // 사용자 ID
    private LocalDateTime expiryTime;  // 세션 만료 시간

    public Session(String sessionId, String userId) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.expiryTime = LocalDateTime.now().plusHours(1);  // 기본 세션 만료 시간을 1시간 후로 설정
    }

    // 세션 유효성 검사
    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiryTime);  // 현재 시간이 만료 시간 이전이면 유효
    }

    // 세션 만료 시간 연장
    public void renew() {
        this.expiryTime = LocalDateTime.now().plusHours(1);  // 만료 시간을 1시간 연장
    }

    // Getter & Setter
    public String getSessionId() {
        return sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }
}

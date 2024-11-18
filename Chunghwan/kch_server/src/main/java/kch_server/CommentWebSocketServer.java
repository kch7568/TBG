package kch_server;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session; // 웹소켓용 세션
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;

import kch_DAO.DAO_User;
import kch_java.SessionManager; // 사용자 세션 관리 매니저

@ServerEndpoint("/commentWebSocket")
public class CommentWebSocketServer {
    private static Set<Session> clients = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) { // 웹소켓 세션
        clients.add(session);
        System.out.println("New client connected: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) { // 웹소켓 세션
        clients.remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            JSONObject receivedJson = new JSONObject(message);
            String postNum = receivedJson.getString("postNum");
            String content = receivedJson.getString("content");
            String sessionId = receivedJson.getString("sessionId");

            // 세션 ID로 닉네임과 프로필 이미지 가져오기
            String nickname = "익명";
            String profileImageUrl = ""; // 기본값 설정
            SessionManager sessionManager = SessionManager.getInstance();
            kch_java.Session userSession = sessionManager.getSession(sessionId);
            String userId = null;

            if (userSession != null) {
                nickname = userSession.getNickname();
                userId = userSession.getUserId();

                // DB에서 프로필 이미지 URL 가져오기
                DAO_User daoUser = new DAO_User();
                profileImageUrl = daoUser.getProfileImageUrl(userId); // getProfileImageUrl 메소드가 userId로 URL을 반환해야 함
            }

            // 댓글을 DB에 저장하고 생성된 댓글 ID 가져오기
            DAO_User daoUser = new DAO_User();
            String commentId = daoUser.saveComment(postNum, userId, content);

            // 클라이언트에게 댓글 전송
            JSONObject commentJson = new JSONObject();
            commentJson.put("author", nickname);
            commentJson.put("content", content);
            commentJson.put("date", "방금 전");
            commentJson.put("profileImageUrl", profileImageUrl);
            commentJson.put("authorId", userId); // 작성자 ID 추가
            commentJson.put("commentId", commentId); // 댓글 ID 추가 (DB에 저장하면서 생성된 ID를 가져옴)

            synchronized (clients) {
                for (Session client : clients) {
                    client.getBasicRemote().sendText(commentJson.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

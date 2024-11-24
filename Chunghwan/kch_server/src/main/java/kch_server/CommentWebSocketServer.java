package kch_server;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session; // 웹소켓용 세션
import javax.websocket.server.ServerEndpoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

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

            // 세션에서 사용자 정보 가져오기
            String nickname = "익명";
            String profileImageUrl = "";
            SessionManager sessionManager = SessionManager.getInstance();
            kch_java.Session userSession = sessionManager.getSession(sessionId);
            String userId = null;

            if (userSession != null) {
                nickname = userSession.getNickname();
                userId = userSession.getUserId();
                DAO_User daoUser = new DAO_User();
                profileImageUrl = daoUser.getProfileImageUrl(userId);
            }

            // 댓글 저장 및 ID 가져오기
            DAO_User daoUser = new DAO_User();
            String commentId = daoUser.saveComment(postNum, userId, content);

            // 댓글 작성자 제외한 모든 사용자에게 FCM 알림
            List<String> fcmTokens = daoUser.getAllFcmTokensExcept(userId);
            for (String token : fcmTokens) {
                sendFcmNotification(token, "새 댓글 알림", nickname + "님의 새 댓글이 등록되었습니다.");
            }

            // 클라이언트에게 댓글 데이터 전송
            JSONObject commentJson = new JSONObject();
            commentJson.put("author", nickname);
            commentJson.put("content", content);
            commentJson.put("date", "방금 전");
            commentJson.put("profileImageUrl", profileImageUrl);
            commentJson.put("authorId", userId);
            commentJson.put("commentId", commentId);

            synchronized (clients) {
                for (Session client : clients) {
                    client.getBasicRemote().sendText(commentJson.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFcmNotification(String token, String title, String body) {
        try {
            // Firebase 알림 구성
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            // Firebase 메시지 전송
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("FCM 메시지 전송 성공: " + response);
        } catch (Exception e) {
            System.out.println("FCM 알림 전송 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }



    
}
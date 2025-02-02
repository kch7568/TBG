package com.cookandroid.tbg;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "comments_channel"; // Notification Channel ID

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "FCM Token: " + token);
        sendTokenToServer(token);
    }

    /**
     * FCM 토큰을 서버에 전송
     */
    private void sendTokenToServer(String token) {
        new Thread(() -> {
            try {
                URL url = new URL("http://43.201.243.50:8080/kch_server/SaveFcmToken"); // 서버 주소
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("sessionId", getSessionId()); // 클라이언트의 세션 ID
                json.put("fcmToken", token);

                try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
                    writer.write(json.toString());
                    writer.flush();
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "FCM 토큰 저장 성공");
                } else {
                    Log.e(TAG, "FCM 토큰 저장 실패, 응답 코드: " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "FCM 토큰 전송 중 오류 발생", e);
            }
        }).start();
    }

    /**
     * 서버에서 전송된 FCM 메시지 처리
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // 알림 데이터 처리
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            String postId = remoteMessage.getData().get("postId"); // 추가 데이터

            Log.d(TAG, "알림 제목: " + title);
            Log.d(TAG, "알림 내용: " + body);

            // 알림 표시
            showNotification(title, body, postId);
        }
    }


    /**
     * 알림을 표시
     */
    private void showNotification(String title, String body, String postId) {
        createNotificationChannel();

        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra("postId", postId); // 게시글 ID 전달
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // FLAG_IMMUTABLE 추가
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.tbg_icon) // 알림 아이콘
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true) // 클릭 시 알림 제거
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body)); // 긴 텍스트 스타일 설정;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }



    /**
     * 알림 채널 생성 (Android 8.0 이상)
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Post Notification";
            String channelDescription = "Notifications for new comments or posts";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }



    /**
     * 세션 ID 가져오기 (SharedPreferences 예시)
     */
    private String getSessionId() {
        return getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("sessionId", null);
    }
}

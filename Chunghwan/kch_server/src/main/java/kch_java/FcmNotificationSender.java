package kch_java;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import kch_DAO.DAO_User;

public class FcmNotificationSender {

    // FCM 알림 전송 메서드
	public static void sendNotification(String fcmToken, String title, String body) {
	    try {
	        Message message = Message.builder()
	                .setToken(fcmToken)
	                .setNotification(Notification.builder()
	                        .setTitle(title)
	                        .setBody(body)
	                        .build())
	                .build();

	        String response = FirebaseMessaging.getInstance().send(message);
	        System.out.println("FCM 메시지 전송 성공: " + response);

	    } catch (FirebaseMessagingException e) {
	        System.err.println("FCM 알림 전송 실패: " + e.getMessage());

	        // 무효화된 토큰 처리
	        if ("UNREGISTERED".equals(e.getMessagingErrorCode())) {
	            System.err.println("무효화된 FCM 토큰 감지: " + fcmToken);
	            deleteInvalidFcmToken(fcmToken);
	        } else {
	            e.printStackTrace();
	        }
	    } catch (Exception e) {
	        System.err.println("알림 전송 중 오류 발생");
	        e.printStackTrace();
	    }
	}


    // 무효화된 FCM 토큰 삭제 메서드
    private static void deleteInvalidFcmToken(String fcmToken) {
        try {
            // DAO_User를 사용하여 데이터베이스에서 무효화된 FCM 토큰 삭제
            DAO_User daoUser = new DAO_User();
            if (daoUser.removeFcmToken(fcmToken)) {
                System.out.println("무효화된 FCM 토큰 삭제 완료: " + fcmToken);
            } else {
                System.err.println("무효화된 FCM 토큰 삭제 실패 또는 존재하지 않음: " + fcmToken);
            }
        } catch (Exception e) {
            System.err.println("FCM 토큰 삭제 중 오류 발생");
            e.printStackTrace();
        }
    }
}

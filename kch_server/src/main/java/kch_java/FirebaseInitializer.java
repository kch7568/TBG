package kch_java;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FirebaseInitializer {
    private static boolean initialized = false;

    public static synchronized void initializeFirebase() {
        if (initialized) {
            System.out.println("Firebase는 이미 초기화되었습니다.");
            return;
        }

        try {
            String path = "firebase-adminsdk.json";
            System.out.println("JSON 파일 경로: " + path);

            FileInputStream serviceAccount = new FileInputStream(path);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            initialized = true;
            System.out.println("Firebase 초기화 성공!");
        } catch (FileNotFoundException e) {
            System.err.println("JSON 파일을 찾을 수 없습니다: " + e.getMessage());
        } catch (NoClassDefFoundError e) {
            System.err.println("의존성 누락: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Firebase 초기화 실패: " + e.getMessage());
        }
    }
}

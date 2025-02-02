package kch_java;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class FirebaseInitializerListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("서버 초기화 중: Firebase 초기화 시작");
        FirebaseInitializer.initializeFirebase();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("서버 종료 중: Firebase 초기화 해제");
    }
}

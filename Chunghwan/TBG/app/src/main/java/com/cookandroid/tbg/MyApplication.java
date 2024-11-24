package com.cookandroid.tbg;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Firebase 초기화
        FirebaseApp.initializeApp(this);
    }
}

package com.example.tbg;

import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import android.view.View;
import android.content.SharedPreferences;

public class SettingActivity extends AppCompatActivity {
    private boolean isInitialLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 광고 및 푸시알림 Switch 버튼 초기화 및 상태 설정
        Switch adSwitch = findViewById(R.id.adSwitch);

        // SharedPreferences에서 광고 상태 불러오기
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean adEnabled = prefs.getBoolean("ad_enabled", true);
        adSwitch.setChecked(adEnabled);

        adSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isInitialLoad) {
                // SharedPreferences에 광고 상태 저장
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("ad_enabled", isChecked);
                editor.apply();

                // 기존 updateAlertStatus 호출 유지
                String newStatus = isChecked ? "true" : "false";
                updateAlertStatus(newStatus);
            }
        });

        isInitialLoad = false;
    }
    private void updateAlertStatus(String status) {
        // 기존의 updateAlertStatus 메소드 내용을 여기에 유지
    }


}
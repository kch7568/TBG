package com.cookandroid.tbg;

import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // XML 레이아웃 파일을 이 액티비티에 연결
        setContentView(R.layout.activity_inf);

        // 뒤로가기 클릭 시 돌아가기
        Button backbtn = findViewById(R.id.backspace);
        backbtn.setOnClickListener(v -> finish());

        // 개인정보 처리방침 TextView 설정
        TextView privacyPolicyText = findViewById(R.id.faqText);
        privacyPolicyText.setText(Html.fromHtml(getString(R.string.privacy_policy_content), Html.FROM_HTML_MODE_LEGACY));

        // 다른 필요한 뷰들에 대한 처리도 여기에 추가할 수 있습니다.
    }
}

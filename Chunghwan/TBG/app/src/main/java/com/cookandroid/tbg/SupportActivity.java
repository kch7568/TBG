package com.cookandroid.tbg;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // XML 레이아웃 파일을 이 액티비티에 연결
        setContentView(R.layout.activity_support);

        // 뒤로가기 클릭 시 돌아가기
        android.widget.Button backbtn = findViewById(R.id.backspace);
        backbtn.setOnClickListener(v -> {
            finish();
        });




        // 다른 필요한 뷰들에 대한 처리도 여기에 추가할 수 있습니다.
    }
}

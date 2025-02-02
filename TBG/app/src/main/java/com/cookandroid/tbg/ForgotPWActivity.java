package com.cookandroid.tbg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPWActivity extends AppCompatActivity {

    public Button backspace;
    public TextView go_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_forgot.xml 레이아웃 파일을 화면으로 설정
        setContentView(R.layout.activity_forgot);

        // XML에서 버튼과 텍스트뷰를 연결
        backspace = findViewById(R.id.backspace);
        go_login = findViewById(R.id.go_loginpage);

        // 뒤로 가기 버튼 클릭 시 이전 화면으로 돌아가는 기능
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티를 종료하여 이전 화면으로 돌아감
            }
        });

        // 로그인 화면으로 이동하는 텍스트 클릭 시 LoginActivity로 이동하는 기능
        go_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPWActivity.this, LoginActivity.class);
                startActivity(intent); // LoginActivity로 이동
                finish(); // ForgotPWActivity를 종료하여 뒤로 가기 시 이 화면으로 돌아오지 않음
            }
        });
    }
}

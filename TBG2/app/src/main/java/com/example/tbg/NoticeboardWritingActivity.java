package com.example.tbg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class NoticeboardWritingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noticeboard_writing);

        // Post_cancle 버튼 초기화
        ImageButton postCancelButton = findViewById(R.id.Post_cancle);
        postCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이전 화면으로 돌아가기
                finish(); // 현재 Activity를 종료하고 이전 Activity로 다시감.
            }
        });
    }
}

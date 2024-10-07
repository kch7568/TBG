package com.cookandroid.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;
    private Button requestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);
        requestButton = findViewById(R.id.requestButton);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest();
            }
        });
    }

    private void makeRequest() {
        // ExecutorService를 사용하여 네트워크 작업을 백그라운드에서 실행
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                String response = "";
                try {
                    // 서블릿 서버와의 연결 설정
                    URL url = new URL("http://10.0.2.2:8888/kch_server/hello");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    // 응답 읽기
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                    response = content.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    response = "Error: " + e.getMessage();
                }

                String finalResponse = response;
                // 메인 스레드에서 UI 업데이트
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        resultTextView.setText(finalResponse);
                    }
                });
            }
        });
    }
}

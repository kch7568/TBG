package com.cookandroid.tbg;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private EditText userIdEditText, passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userIdEditText = findViewById(R.id.userIdEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userIdEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // 서버로 로그인 요청 보내기
                new Thread(() -> {
                    String serverUrl = "http://10.0.2.2:8888/kch_server/login";  // 서버 URL

                    try {
                        URL url = new URL(serverUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setDoOutput(true);

                        // 서버로 보낼 파라미터 설정
                        String postData = "userId=" + userId + "&password=" + password;

                        OutputStream os = conn.getOutputStream();
                        os.write(postData.getBytes());
                        os.flush();
                        os.close();

                        // 서버 응답 받기
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // 서버 응답을 UI에 표시
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_LONG).show());

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error during login.", Toast.LENGTH_LONG).show());
                    }
                }).start();
            }
        });
    }
}

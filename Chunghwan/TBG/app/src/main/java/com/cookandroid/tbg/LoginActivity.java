package com.cookandroid.tbg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;






public class LoginActivity extends AppCompatActivity {
    private EditText userIdEditText, passwordEditText;
    public TextView forgotPW, register;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userIdEditText = findViewById(R.id.idText);
        passwordEditText = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.LoginButton);
        forgotPW = findViewById(R.id.findid);
        register = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userIdEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // 서버로 로그인 요청 보내기
                new Thread(() -> {
                    String serverUrl = "http://10.0.2.2:8888/kch_server/login";

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

                        int responseCode = conn.getResponseCode();
                        System.out.println("Response Code: " + responseCode);

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();

                            // 서버 응답을 JSON으로 파싱
                            JSONObject jsonResponse = new JSONObject(response.toString());
                            String message = jsonResponse.getString("message");
                            String sessionId = jsonResponse.optString("sessionId", null);

                            // 메시지를 Toast로 표시
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show());

                            // 로그인 성공 시 세션 ID 저장 후 메인 화면으로 이동
                            if ("로그인에 성공하셨습니다!".equals(message) && sessionId != null) {
                                SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                                prefs.edit().putString("sessionId", sessionId).apply();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error during login.", Toast.LENGTH_LONG).show());
                    }
                }).start();
            }
        });

        register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        forgotPW.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPWActivity.class);
            startActivity(intent);
        });
    }
}

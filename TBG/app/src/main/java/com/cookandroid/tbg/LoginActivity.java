package com.cookandroid.tbg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

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
                    String serverUrl = "http://43.201.243.50:8080/kch_server/login";

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

                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(task -> {
                                            if (!task.isSuccessful()) {
                                                Log.w("FCM", "FCM 토큰 가져오기 실패", task.getException());
                                                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "FCM 토큰 가져오기 실패", Toast.LENGTH_SHORT).show());
                                                return;
                                            }

                                            String token = task.getResult();
                                            if (token != null) {
                                                Log.d("FCM", "FCM 토큰: " + token);
                                                sendTokenToServer(token);
                                            } else {
                                                Log.e("FCM", "FCM 토큰이 null입니다.");
                                            }
                                        });


                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // 애니메이션 비활성화
                                startActivity(intent);
                                overridePendingTransition(0, 0); // 전환 애니메이션 제거

                            }
                        } else {
                            Log.e("로그인에러?", String.valueOf(responseCode));
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("로그인에러?", e.toString());
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
    private void sendTokenToServer(String token) {
        new Thread(() -> {
            try {
                URL url = new URL("http://43.201.243.50:8080/kch_server/SaveFcmToken");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                // JSON 데이터 생성
                JSONObject json = new JSONObject();
                SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String sessionId = prefs.getString("sessionId", null);

                if (sessionId == null) {
                    Log.e("FCM", "세션 ID가 존재하지 않습니다.");
                    return;
                }


                json.put("sessionId", sessionId); // SharedPreferences에서 가져온 sessionId
                json.put("fcmToken", token); // FCM 토큰

                // 서버로 데이터 전송
                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("FCM", "토큰 전송 성공");
                } else {
                    Log.e("FCM", "토큰 전송 실패, 응답 코드: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("FCM", "FCM 토큰 전송 중 오류 발생", e);
            }
        }).start();
    }


}
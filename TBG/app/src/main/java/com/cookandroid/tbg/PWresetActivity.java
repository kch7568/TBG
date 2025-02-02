package com.cookandroid.tbg;

import android.content.SharedPreferences;
import android.os.Bundle;
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

public class PWresetActivity extends AppCompatActivity {

    private EditText oldPasswordInput;
    private EditText newPasswordInput;
    private EditText confirmPasswordInput;
    private TextView pwGood;
    private TextView newPwGood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // XML 레이아웃 파일을 이 액티비티에 연결
        setContentView(R.layout.activity_pwreset);

        // 뒤로가기 클릭 시 돌아가기
        Button backbtn = findViewById(R.id.backspace);
        backbtn.setOnClickListener(v -> finish());

        // 뷰 초기화
        oldPasswordInput = findViewById(R.id.oldPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        pwGood = findViewById(R.id.pw_good);
        newPwGood = findViewById(R.id.newpw_good);
        Button changePasswordButton = findViewById(R.id.changePasswordButton);

        changePasswordButton.setOnClickListener(v -> {
            String oldPassword = oldPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(PWresetActivity.this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            String sessionId = prefs.getString("sessionId", null);

            if (sessionId != null) {
                new Thread(() -> {
                    String serverUrl = "http://43.201.243.50:8080/kch_server/PasswordReset";
                    try {
                        URL url = new URL(serverUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                        conn.setDoOutput(true);

                        String postData = "sessionId=" + sessionId +
                                "&currentPassword=" + oldPassword +
                                "&newPassword=" + newPassword +
                                "&confirmPassword=" + confirmPassword;
                        OutputStream os = conn.getOutputStream();
                        os.write(postData.getBytes("UTF-8"));
                        os.flush();
                        os.close();

                        int responseCode = conn.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();

                            JSONObject jsonResponse = new JSONObject(response.toString());
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");

                            runOnUiThread(() -> {
                                if ("success".equals(status)) {
                                    Toast.makeText(PWresetActivity.this, message, Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(PWresetActivity.this, message, Toast.LENGTH_LONG).show();
                                    if ("fail".equals(status)) {
                                        if (message.contains("현재 비밀번호")) {
                                            pwGood.setText("현재 비밀번호가 올바르지 않습니다.");
                                        } else if (message.contains("새 비밀번호")) {
                                            newPwGood.setText("새 비밀번호가 일치하지 않습니다.");
                                        }
                                    }
                                }
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(PWresetActivity.this, "서버 오류: " + responseCode, Toast.LENGTH_LONG).show());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(PWresetActivity.this, "요청 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show());
                    }
                }).start();
            } else {
                Toast.makeText(this, "세션 ID를 찾을 수 없습니다. 다시 로그인하세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

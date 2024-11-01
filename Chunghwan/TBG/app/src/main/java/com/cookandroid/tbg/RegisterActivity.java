package com.cookandroid.tbg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class RegisterActivity extends AppCompatActivity {
    private EditText userIdEditText, passwordEditText, PW,password_good, nicknameEditText;
    public TextView Login_page;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userIdEditText = findViewById(R.id.userIdEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        PW = findViewById(R.id.PW);  //임의추가
        TextView passwordGoodTextView = findViewById(R.id.password_good); //임의추가
        nicknameEditText = findViewById(R.id.nicknameText);
        registerButton = findViewById(R.id.registerButton);
        Login_page = findViewById(R.id.go_loginpage);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                String userId = userIdEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String password_re = PW.getText().toString();  // 비밀번호 확인 필드 값 가져오기
                String nickname = nicknameEditText.getText().toString();
                if(password.equals(password_re)){
                    passwordGoodTextView.setText("비밀번호가 같습니다");

                }else {
                    // 비밀번호가 일치하지 않으면 비워두거나 다른 메시지를 표시
                    passwordGoodTextView.setText("비밀번호가 일치하지 않습니다.");
                }
                // 서버로 회원가입 요청 보내기
                new Thread(() -> {
                    String serverUrl = "http://10.0.2.2:8888/kch_server/register";  // 서버 URL

                    try {
                        URL url = new URL(serverUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setDoOutput(true);

                        // 서버로 보낼 파라미터 설정
                        String postData = "userId=" + URLEncoder.encode(userId, StandardCharsets.UTF_8.toString()) +
                                "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8.toString()) +
                                "&password_re=" + URLEncoder.encode(password_re, StandardCharsets.UTF_8.toString()) +
                                "&nickname=" + URLEncoder.encode(nickname, StandardCharsets.UTF_8.toString());

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
                        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, response.toString(), Toast.LENGTH_LONG).show());
                        if (response.toString().equals("회원가입에 성공 하셨습니다!")) {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class); // 로그인으로 이동
                            startActivity(intent);
                            finish();  // 현재 액티비티를 종료해서 뒤로 가기 버튼 눌렀을 때 다시 이 화면이 나오지 않게 함
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Error during registration." + e.toString(), Toast.LENGTH_LONG).show());
                    }
                }).start();



            }
        });
        Login_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 페이지로 이동하는 코드
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);  // LoginActivity로 이동
                startActivity(intent);
            }
        });

        
    }
}

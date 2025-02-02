package com.cookandroid.tbg;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppMainActivity extends AppCompatActivity {
    private TextView nicknameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmain);

        nicknameTextView = findViewById(R.id.nicknameTextView);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String sessionId = prefs.getString("sessionId", null);

        if (sessionId != null) {
            // 서버에 닉네임 요청
            new Thread(() -> {
                String serverUrl = "http://43.201.243.50:8080/kch_server/MainServlet";

                try {
                    URL url = new URL(serverUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    conn.setDoOutput(true);

                    // sessionId를 POST 요청의 본문에 추가
                    String postData = "sessionId=" + sessionId;
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
                        if ("success".equals(jsonResponse.getString("status"))) {
                            String nickname = jsonResponse.getString("nickname");
                            runOnUiThread(() -> nicknameTextView.setText("안녕하세요, " + nickname + "님!!"));
                        } else {
                            String message = jsonResponse.getString("message");
                            runOnUiThread(() -> Toast.makeText(AppMainActivity.this, message, Toast.LENGTH_LONG).show());
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(AppMainActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(AppMainActivity.this, "Error during request.", Toast.LENGTH_LONG).show());
                }
            }).start();
        } else {
            Toast.makeText(this, "Session ID not found. Please log in.", Toast.LENGTH_SHORT).show();
        }
    }
}


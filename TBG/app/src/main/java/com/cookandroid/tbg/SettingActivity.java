package com.cookandroid.tbg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SettingActivity extends AppCompatActivity {

    private boolean isInitialLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_setting);

        // 뒤로가기 클릭 시 돌아가기
        Button backbtn = findViewById(R.id.backspace);
        backbtn.setOnClickListener(v -> finish());

        // LinearLayout 클릭 시 새로운 Activity로 이동하는 Intent 설정
        LinearLayout faqLayout = findViewById(R.id.question);
        faqLayout.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, FaqActivity.class);
            startActivity(intent);
        });

        LinearLayout informationLayout = findViewById(R.id.information);
        informationLayout.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, InformationActivity.class);
            startActivity(intent);
        });

        // 푸시알림 Switch 버튼 초기화 및 상태 설정
        Switch adSwitch = findViewById(R.id.adSwitch);

////////////////////////////////////////
        // 광고알림 Switch 버튼 초기화 및 상태 설정
        Switch adSwitch2 = findViewById(R.id.adswitch2);

        // SharedPreferences에서 광고 상태 불러오기
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean adEnabled = prefs.getBoolean("ad_enabled", true);
        adSwitch2.setChecked(adEnabled);

        adSwitch2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isInitialLoad) {
                // SharedPreferences에 광고 상태 저장
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("ad_enabled", isChecked);
                editor.apply();
            }
        });


////////////////////////////////////////


        // SharedPreferences에서 User ID 가져오기
        String sessionId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("sessionId", null);
        if (sessionId != null) {
            new Thread(() -> {
                try {
                    String serverUrl = "http://43.201.243.50:8080/kch_server/GetAlertStatus";
                    URL url = new URL(serverUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    conn.setDoOutput(true);

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
                            boolean alertStatus = jsonResponse.getBoolean("alertStatus");
                            runOnUiThread(() -> {
                                adSwitch.setChecked(alertStatus);
                                isInitialLoad = false; // Set to false after the initial status is loaded
                            });
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "서버 오류: " + responseCode, Toast.LENGTH_LONG).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(this, "알림 상태 가져오기 실패", Toast.LENGTH_LONG).show());
                }
            }).start();
        } else {
            Toast.makeText(this, "세션 ID를 찾을 수 없습니다. 다시 로그인하세요.", Toast.LENGTH_SHORT).show();
        }

        // 광고 및 푸시알림 Switch 버튼 클릭 시 DB 업데이트
        adSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isInitialLoad) { // Only show Toast when it's a user-initiated change
                String newStatus = isChecked ? "true" : "false";
                updateAlertStatus(newStatus);
            }
        });

        // 로그아웃 기능
        LinearLayout logoutLayout = findViewById(R.id.pwReset);
        logoutLayout.setOnClickListener(v -> {
            // 로그아웃 처리 (SharedPreferences에 저장된 세션 ID 제거)
            getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().remove("sessionId").apply();
            Toast.makeText(this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // 회원 탈퇴 기능
        TextView deleteAccountLayout = findViewById(R.id.deleteAccount);
        deleteAccountLayout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("회원 탈퇴")
                    .setMessage("정말 탈퇴하시겠습니까?")
                    .setPositiveButton("예", (dialog, which) -> deleteAccount(sessionId))
                    .setNegativeButton("아니오", null)
                    .show();
        });
    }

    private void updateAlertStatus(String status) {
        new Thread(() -> {
            try {
                String serverUrl = "http://43.201.243.50:8080/kch_server/UpdateAlertStatus";
                URL url = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                conn.setDoOutput(true);

                // 세션 ID 가져오기
                String sessionId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("sessionId", null);
                if (sessionId == null) {
                    runOnUiThread(() -> Toast.makeText(this, "세션 ID를 찾을 수 없습니다. 다시 로그인하세요.", Toast.LENGTH_SHORT).show());
                    return;
                }

                String postData = "sessionId=" + sessionId + "&status=" + status;
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
                    String serverMessage = jsonResponse.getString("message");
                    runOnUiThread(() -> Toast.makeText(this, serverMessage, Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "서버 오류: " + responseCode, Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "요청 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void deleteAccount(String sessionId) {
        new Thread(() -> {
            try {
                String serverUrl = "http://43.201.243.50:8080/kch_server/DeleteAccount";
                URL url = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                conn.setDoOutput(true);

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
                        runOnUiThread(() -> {
                            Toast.makeText(this, "계정이 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            // 로그아웃 처리 후 로그인 화면으로 이동
                            getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().remove("sessionId").apply();
                            Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        String serverMessage = jsonResponse.getString("message");
                        runOnUiThread(() -> Toast.makeText(this, serverMessage, Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "서버 오류: " + responseCode, Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "요청 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}


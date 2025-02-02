package com.cookandroid.tbg;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cookandroid.tbg.databinding.ActivityHomeBinding;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    private HomeFragment homeFragment;
    private AssignmentFragment assignmentFragment;
    private Accountfragment accountFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Fragments 초기화
        homeFragment = new HomeFragment();
        assignmentFragment = new AssignmentFragment();
        accountFragment = new Accountfragment();

        // 초기 Fragment 설정
        replaceFragment(homeFragment);


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                showFragment(homeFragment);
            } else if (itemId == R.id.assignment) {
                showFragment(assignmentFragment);
            } else if (itemId == R.id.profile) {
                showFragment(accountFragment);
            }

            return true;
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
                            Log.d("NicknameUpdate", "Setting nickname to: " + nickname);
                            // 초기 텍스트 설정
                            runOnUiThread(() -> homeFragment.setWelcomeMessageText("환영합니다, " + nickname + "님!!"));

                        } else {
                            String message = jsonResponse.getString("message");
                            runOnUiThread(() -> Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show());
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(HomeActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(HomeActivity.this, "Error during request.", Toast.LENGTH_LONG).show());
                }
            }).start();
        } else {
            Toast.makeText(this, "Session ID not found. Please log in.", Toast.LENGTH_SHORT).show();
        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_layout, fragment);
        transaction.commit();
    }

    private void showFragment(Fragment fragmentToShow) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // 모든 Fragment를 숨김
        if (homeFragment.isAdded()) transaction.hide(homeFragment);
        if (assignmentFragment.isAdded()) transaction.hide(assignmentFragment);
        if (accountFragment.isAdded()) transaction.hide(accountFragment);

        // 선택한 Fragment만 표시
        if (!fragmentToShow.isAdded()) {
            transaction.add(R.id.frame_layout, fragmentToShow);
        }
        transaction.show(fragmentToShow);
        transaction.commit();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // WindowManager 관련 리소스 정리
        if (getWindow() != null) {
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

}

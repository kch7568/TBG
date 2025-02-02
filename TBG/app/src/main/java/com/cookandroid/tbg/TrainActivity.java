package com.cookandroid.tbg;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TrainActivity extends AppCompatActivity {

    private Button train_between_addBtn, train_between_minusBtn;
    private TextView train_between_tv_count;
    private int count = 0;

    ImageView go_busBtn;
    ImageView go_airportBtn;
    private TextView selectedDateText;
    private Button selectDateButton;

    private RadioGroup train_type;
    private String trainType="";
    private String trainGradeCode="";
    private static final int REQUEST_CODE_START = 1; // 출발지 요청 코드
    private static final int REQUEST_CODE_ARRIVE = 2; // 도착지 요청 코드

    private TextView startTextView;
    private TextView arriveTextView;

    private boolean isRoundTrip = true;


    @SuppressLint({"MissingInflatedId", "SetTextI18n", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.train);

        // 뒤로가기 클릭 시 돌아가기
        Button backbtn = findViewById(R.id.backspace);
        backbtn.setOnClickListener(v -> finish());

        startTextView = findViewById(R.id.start_Place2);
        arriveTextView = findViewById(R.id.arrive_Place2);

        // returnText 초기화
        train_type = findViewById(R.id.radioGroup);
        train_type.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            RadioButton selected = findViewById(checkedId);
            if (selected != null) {
                trainType = selected.getText().toString();
            }
            // trainType 값을 API 호환 값으로 변환
            switch (trainType) {
                case "KTX":
                    trainGradeCode = "00";
                    break;
                case "산천-A":
                    trainGradeCode = "07";
                    break;
                case "새마을호-ITX":
                    trainGradeCode = "08";
                    break;
                case "무궁화호":
                    trainGradeCode = "02";
                    break;
                default:
                    trainType = ""; // 기본값 (좌석 클래스가 선택되지 않았을 경우)
            }
            Log.d("열차", trainType);
        });

        Button btnSearch = findViewById(R.id.between_serch_icon);
        btnSearch.setOnClickListener(v -> {
            String depPlaceId = getStationId(startTextView.getText().toString());
            String arrPlaceId = getStationId(arriveTextView.getText().toString());
            String depPlandTime = formatDate(selectedDateText.getText().toString());

            Log.d("시간 : " , depPlandTime);
            Log.d("시작 : " , arrPlaceId);
            Log.d("도착 : " , depPlaceId);


            // 필수 입력값 검증
            if (startTextView.getText().toString().isEmpty()) {
                Toast.makeText(this, "출발지를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (arriveTextView.getText().toString().isEmpty()) {
                Toast.makeText(this, "도착지를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (depPlandTime.isEmpty() || depPlandTime.equals("가는 날")) {
                Toast.makeText(this, "출발 날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (count <= 0) {
                Toast.makeText(this, "탑승객 수를 설정해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (trainGradeCode.isEmpty()) {
                Toast.makeText(this, "열차 종류를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }


            fetchTrainData(depPlaceId, arrPlaceId, depPlandTime);
        });

        go_busBtn = findViewById(R.id.go_busBtn);
        go_busBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BusActivity.class);
                startActivity(intent);
            }
        });
        go_airportBtn = findViewById(R.id.go_airportBtn);
        go_airportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AirportMainActivity.class);
                startActivity(intent);
            }
        });

        // 출발지를 설정하기 위한 클릭 이벤트
        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PracticeActivity를 실행하여 결과를 받음
                Intent intent = new Intent(TrainActivity.this, TrainStartPlaceActivity.class);
                startActivityForResult(intent, REQUEST_CODE_START);
            }
        });

        // 도착지를 설정하기 위한 클릭 이벤트
        arriveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PracticeActivity를 실행하여 결과를 받음
                Intent intent = new Intent(TrainActivity.this, TrainArrivePlaceActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ARRIVE);
            }
        });

        train_between_tv_count = findViewById(R.id.train_between_tv_count);
        train_between_tv_count.setText(count + "");
        train_between_addBtn = findViewById(R.id.train_between_addBtn);
        train_between_minusBtn = findViewById(R.id.train_between_minusBtn);

        train_between_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count >=9){
                    return;
                }
                count++;
                train_between_tv_count.setText(count + "");
            }
        });

        train_between_minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 0) {
                    count--;
                    train_between_tv_count.setText(String.valueOf(count));
                }
            }
        });

        Locale locale = new Locale("ko");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        selectedDateText = findViewById(R.id.train_between_Startcalendar);
        selectDateButton = findViewById(R.id.train_between_Startcalendar);

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 날짜를 기준으로 설정
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // DatePickerDialog 생성
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TrainActivity.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // 선택된 날짜를 TextView에 표시
                            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                            selectedDateText.setText(date);

                        },
                        year, month, day
                );
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

    }

    private String getStationId(String stationName) {
        Map<String, String> stationMap = new HashMap<>();
        stationMap.put("서울역", "NAT010000");//
        stationMap.put("용산역", "NAT010032");//
        stationMap.put("부산역", "NAT014445");//
        stationMap.put("여수역", "NAT041993");//여수 -> 부산,서울(에러) 없음
        stationMap.put("대전역", "NAT011668"); //
        stationMap.put("광주송정역", "NAT031857");//
        stationMap.put("판교역", "NAT250007"); // 충주만감  행신 /[서울]/ 용산 
        stationMap.put("행신역", "NAT110147"); //
        stationMap.put("목포역", "NAT032563"); //
        stationMap.put("강릉역", "NAT601936");//
        stationMap.put("충주역", "NAT050827"); //
        stationMap.put("익산역", "NAT030879"); //
        stationMap.put("포항역", "NAT8B0351");//

        // 다른 역들도 추가하고 싶으면 위와 같이 추가하면 됩니다.

        return stationMap.getOrDefault(stationName, "UNKNOWN");
    }


    private String formatDate(String date) {
        // 입력이 유효한지 확인
        if (date == null || !date.contains("-")) {
            return ""; // 잘못된 날짜 형식인 경우 빈 문자열 반환
        }

        try {
            // "YYYY-MM-DD" 형식을 "YYYYMMDD" 형식으로 변환
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(date));
        } catch (Exception e) {
            Log.e("formatDate", "Invalid date format: " + date, e);
            return ""; // 예외 발생 시 빈 문자열 반환
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            String stationName2 = data.getStringExtra("stationName2");

            if (requestCode == REQUEST_CODE_START) {
                // 출발지 데이터 설정
                startTextView.setText(stationName2);
            } else if (requestCode == REQUEST_CODE_ARRIVE) {
                // 도착지 데이터 설정
                arriveTextView.setText(stationName2);
            }
        }
    }
    private void fetchTrainData(String depPlaceId, String arrPlaceId, String depPlandTime) {
        new Thread(() -> {
            try {
                runOnUiThread(() -> Toast.makeText(this, "요청을 보내는 중입니다...", Toast.LENGTH_SHORT).show());


                String serviceKey = ""; // 공공데이터포털에서 받은 인증키
                String urlStr = "http://apis.data.go.kr/1613000/TrainInfoService/getStrtpntAlocFndTrainInfo" +
                        "?serviceKey=" + serviceKey +
                        "&depPlaceId=" + depPlaceId +
                        "&arrPlaceId=" + arrPlaceId +
                        "&depPlandTime=" + depPlandTime +
                        "&trainGradeCode=" + trainGradeCode +
                        "&numOfRows=50&pageNo=1&_type=json";


                Log.d("TrainActivity", "depPlaceId: " + depPlaceId);
                Log.d("TrainActivity", "arrPlaceId: " + arrPlaceId);
                Log.d("TrainActivity", "depPlandTime: " + depPlandTime);


                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader rd;
                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                conn.disconnect();
                String result = sb.toString();

                // JSON 파싱 및 결과 처리
                JSONObject jsonResponse = new JSONObject(result);
                JSONObject response = jsonResponse.getJSONObject("response");
                JSONObject body = response.getJSONObject("body");




                // 'items' 확인 및 처리
                String itemsString = body.optString("items", "");
                if (itemsString.isEmpty() || itemsString.equals("\"\"")) {
                    Log.e("fetchBusData", "Items is empty or not present.");
                    runOnUiThread(() -> Toast.makeText(this, "검색된 버스 정보가 없습니다.", Toast.LENGTH_SHORT).show());
                    return;
                }


                // 'items' 객체 내에서 'item' 배열을 추출
                JSONArray items = new JSONArray();
                if (body.has("items")) {
                    JSONObject itemsObject = body.getJSONObject("items");
                    if (itemsObject.has("item")) {
                        items = itemsObject.getJSONArray("item");
                    }
                }

                if (items == null || items.length() == 0) {
                    Log.e("fetchTrainData", "No bus data found.");
                    runOnUiThread(() -> Toast.makeText(this, "검색된 버스 정보가 없습니다.", Toast.LENGTH_SHORT).show());
                    return;
                }

                Log.d("fetchTrainData", "Response Body: " + response.toString());


                // 데이터를 SharedPreferences에 저장
                getSharedPreferences("TrainData", MODE_PRIVATE)
                        .edit()
                        .clear()
                        .putString("response", response.toString())
                        .apply();


                runOnUiThread(() -> {
                    Intent intent = new Intent(TrainActivity.this, TrainResultActivity.class);
                    intent.putExtra("startTextkr", startTextView.getText().toString());
                    intent.putExtra("arriveTextkr", arriveTextView.getText().toString());
                    intent.putExtra("humanCount",train_between_tv_count.getText().toString()); //텍스트로 뿌려줄거
                    intent.putExtra("startTime",depPlandTime);
                    intent.putExtra("count",count);//정수
                    Log.d("depPlandTime",depPlandTime);
                    startActivity(intent);

                });

            } catch (Exception e) {
                Log.e("fetchTrainData", "Error: " + e.getMessage());
            }
        }).start();
    }




}
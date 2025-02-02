package com.cookandroid.tbg;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
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

public class BusActivity extends AppCompatActivity {
    private Button bus_oneway_addBtn, bus_oneway_minusBtn;
    private TextView bus_oneway_count;
    private int count = 0;
    private TextView selectedDateText;
    private Button selectDateButton;
    private static final int REQUEST_CODE_START = 1;
    private static final int REQUEST_CODE_ARRIVE = 2;
    private TextView startTextView;
    private TextView arriveTextView;
    private String busType;
    private RadioGroup bus_type;
    private String gradeid;
    private ImageView go_trainBtn, go_airportBtn;

    @SuppressLint({"MissingInflatedId", "SetTextI18n", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus);

        // 뒤로가기 클릭 시 돌아가기
        Button backbtn = findViewById(R.id.backspace);
        backbtn.setOnClickListener(v -> finish());


        startTextView = findViewById(R.id.start_bus);
        arriveTextView = findViewById(R.id.arrive_bus);

        bus_type = findViewById(R.id.bus_seat_group);
        bus_type.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            if(selectedRadioButton != null){
                busType = selectedRadioButton.getText().toString();
            }
            switch(busType){
                case "우등석":
                    gradeid = "1";
                    break;
                case "프리미엄석":
                    gradeid = "7";
                    break;
                case "심야우등석":
                    gradeid = "3";
                    break;
                case "심야프리미엄석":
                    gradeid = "4";
                    break;
                default:
                    busType = "";
            }
            Log.d("bustype",busType);
        });

        Button btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(v -> {
            String depTerminalId = getTerminalId(startTextView.getText().toString());
            String arrTerminalId = getTerminalId(arriveTextView.getText().toString());
            String depPlandTime = formatDate(selectedDateText.getText().toString());

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
            if (gradeid == null || gradeid.isEmpty() || busType.isEmpty()) {
                Toast.makeText(this, "버스 종류를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            fetchBusData(depTerminalId, arrTerminalId, depPlandTime, gradeid);
        });


        // 출발지를 설정하기 위한 클릭 이벤트
        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PracticeActivity를 실행하여 결과를 받음
                Intent intent = new Intent(BusActivity.this, BusStartPlaceActivity.class);
                startActivityForResult(intent, REQUEST_CODE_START);
            }
        });

        // 도착지를 설정하기 위한 클릭 이벤트
        arriveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PracticeActivity를 실행하여 결과를 받음
                Intent intent = new Intent(BusActivity.this, BusArrivePlaceActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ARRIVE);
            }
        });

        bus_oneway_count = findViewById(R.id.bus_oneway_count);
        bus_oneway_count.setText(count + "");
        bus_oneway_addBtn = findViewById(R.id.bus_oneway_addBtn);
        bus_oneway_minusBtn = findViewById(R.id.bus_oneway_minusBtn);

        bus_oneway_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count >= 9){
                    return;
                }
                count++;
                bus_oneway_count.setText(count + "");
            }
        });
        bus_oneway_minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 0) {
                    count--;
                    bus_oneway_count.setText(String.valueOf(count));
                }
            }
        });

        go_trainBtn = findViewById(R.id.go_trainBtn);
        go_trainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainActivity.class);
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


        selectedDateText = findViewById(R.id.bus_Startcalendar);
        selectDateButton = findViewById(R.id.bus_Startcalendar);

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
                        BusActivity.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // 선택된 날짜를 "yyyy-MM-dd" 형식으로 포맷팅
                            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                            selectedDateText.setText(date); // TextView에 포맷팅된 날짜를 설정
                        },
                        year, month, day
                );
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

    }

    private String getTerminalId(String terminalName) {
        Map<String, String> terminalMap = new HashMap<>();
        terminalMap.put("서울경부 터미널", "NAEK010");
        terminalMap.put("부산 터미널", "NAEK700");
        terminalMap.put("동대구 터미널", "NAEK801");
        terminalMap.put("광주 터미널", "NAEK500"); //유스퀘어
        terminalMap.put("인천 터미널", "NAEK100");
        terminalMap.put("부천 터미널", "NAEK101");
        terminalMap.put("서울동부 터미널", "NAEK032");//동서울
        terminalMap.put("공주 터미널", "NAEK320");
        terminalMap.put("천안 터미널", "NAEK310");
        terminalMap.put("아산 터미널", "NAEK343");
        terminalMap.put("구미 터미널", "NAEK810");
        terminalMap.put("마산 터미널", "NAEK705");
        terminalMap.put("원주 터미널", "NAEK240");
        terminalMap.put("포천 터미널", "NAEK146");



        // 다른 터미널들도 추가
        return terminalMap.getOrDefault(terminalName, "UNKNOWN");
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
            String busName = data.getStringExtra("busName");
            if (requestCode == REQUEST_CODE_START) {
                startTextView.setText(busName);
            } else if (requestCode == REQUEST_CODE_ARRIVE) {
                arriveTextView.setText(busName);
            }
        }
    }

    private void fetchBusData(String depTerminalId, String arrTerminalId, String depPlandTime, String gradeid) {
        new Thread(() -> {
            try {
                runOnUiThread(() -> Toast.makeText(this, "요청을 보내는 중입니다...", Toast.LENGTH_SHORT).show());

                String serviceKey = "";
                String urlStr = "http://apis.data.go.kr/1613000/ExpBusInfoService/getStrtpntAlocFndExpbusInfo"
                        + "?serviceKey=" + serviceKey
                        + "&depTerminalId=" + depTerminalId
                        + "&arrTerminalId=" + arrTerminalId
                        + "&depPlandTime=" + depPlandTime
                        + "&busGradeId=" + gradeid
                        + "&numOfRows=30&pageNo=1&_type=json";

                Log.d("테스트",depTerminalId );
                Log.d("테스트",arrTerminalId );
                Log.d("테스트",depPlandTime );
                Log.d("테스트",gradeid );

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

                Log.d("응답", result);

                JSONObject jsonResponse = new JSONObject(result);
                JSONObject response = jsonResponse.getJSONObject("response");
                if (response == null) {
                    Log.e("fetchBusData", "Response is null");
                    runOnUiThread(() -> Toast.makeText(this, "API 응답에 문제가 있습니다.", Toast.LENGTH_SHORT).show());
                    return;
                }


                Log.d("응답", response.toString());

                JSONObject body = response.getJSONObject("body");
                if (body == null) {
                    Log.e("fetchBusData", "Body is null");
                    runOnUiThread(() -> Toast.makeText(this, "검색된 버스 정보가 없습니다.", Toast.LENGTH_SHORT).show());
                    return;
                }


                // 'items' 확인 및 처리
                String itemsString = body.optString("items", "");
                if (itemsString.isEmpty() || itemsString.equals("\"\"")) {
                    Log.e("fetchBusData", "Items is empty or not present.");
                    runOnUiThread(() -> Toast.makeText(this, "검색된 버스 정보가 없습니다.", Toast.LENGTH_SHORT).show());
                    return;
                }




                JSONArray items = new JSONArray();
                if (body.has("items")) {
                    JSONObject itemsObject = body.getJSONObject("items");
                    if (itemsObject.has("item")) {
                        items = itemsObject.getJSONArray("item");
                    }
                }

                if (items == null || items.length() == 0) {
                    Log.e("fetchBusData", "No bus data found.");
                    runOnUiThread(() -> Toast.makeText(this, "검색된 버스 정보가 없습니다.", Toast.LENGTH_SHORT).show());
                    return;
                }

                getSharedPreferences("BusData", MODE_PRIVATE)
                        .edit()
                        .clear()
                        .putString("response", response.toString())
                        .apply();

                runOnUiThread(() -> {
                    Intent intent = new Intent(BusActivity.this, BusResultActivity.class);
                    intent.putExtra("startTextkr", startTextView.getText().toString());
                    intent.putExtra("arriveTextkr", arriveTextView.getText().toString());
                    intent.putExtra("humanCount",  bus_oneway_count.getText().toString());
                    intent.putExtra("count",count);//정수
                    startActivity(intent);
                });
            } catch (Exception e) {
                Log.e("fetchBusData", "Error: " + e.getMessage());
            }
        }).start();
    }
}

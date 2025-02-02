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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AirportMainActivity extends AppCompatActivity {

    private Button airport_between_addBtn, airport_between_minusBtn;
    private TextView airport_between_count;
    private int count = 0;

    Button airport_onewayBtn, airport_betweenBtn;

    ImageView go_trainBtn;
    ImageView go_busBtn;

    private TextView selectedDateText;
    private Button selectDateButton;

    private TextView selectedDateText2;
    private Button selectDateButton2;

    private RadioGroup etClass;
    private String seatClass ="";
    private String seatClasskr ="";
    private static final int REQUEST_CODE_START = 1; // 출발지 요청 코드
    private static final int REQUEST_CODE_ARRIVE = 2; // 도착지 요청 코드

    private TextView startTextView;
    private TextView arriveTextView;
    private boolean isRoundTrip = true;
    // 클래스 필드에 선언
    private TextView returnText;


    @SuppressLint({"MissingInflatedId", "SetTextI18n", "WrongViewCast", "CutPasteId"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.airport_main);

        // 뒤로가기 클릭 시 돌아가기
        Button backbtn = findViewById(R.id.backspace);
        backbtn.setOnClickListener(v -> finish());

        startTextView = findViewById(R.id.start_airport);
        arriveTextView = findViewById(R.id.arrive_airport);
        // returnText 초기화
        returnText = findViewById(R.id.airport_between_Arrivecalendar); // 돌아오는 날짜를 표시하는 TextView
        etClass = findViewById(R.id.radioGroup);
        etClass.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            RadioButton selected = findViewById(checkedId);
            if (selected != null) {
                seatClass = selected.getText().toString();
            }
            // seatClass 값을 API 호환 값으로 변환
            switch (seatClass) {
                case "일반석":
                    seatClasskr = "일반석";
                    seatClass = "ECONOMY";
                    break;
                case "프리미엄 일반석":
                    seatClasskr = "프리미엄 일반석";
                    seatClass = "PREMIUM_ECONOMY";
                    break;
                case "비즈니스석":
                    seatClasskr = "비즈니스석";
                    seatClass = "BUSINESS";
                    break;
                case "일등석":
                    seatClasskr = "일등석";
                    seatClass = "FIRST";
                    break;
                default:
                    seatClasskr = "알 수 없음";
                    seatClass = ""; // 기본값 (좌석 클래스가 선택되지 않았을 경우)
            }
            Log.d("좌석", seatClass);
            Log.d("좌석", seatClasskr);
        });







        // 출발지를 설정하기 위한 클릭 이벤트
        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PracticeActivity를 실행하여 결과를 받음
                Intent intent = new Intent(AirportMainActivity.this, AirportStartPlaceActivity.class);
                startActivityForResult(intent, REQUEST_CODE_START);
            }
        });

        // 도착지를 설정하기 위한 클릭 이벤트
        arriveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PracticeActivity를 실행하여 결과를 받음
                Intent intent = new Intent(AirportMainActivity.this, AirportArrivePlaceActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ARRIVE);
            }
        });

        airport_between_count = findViewById(R.id.airport_between_count);
        airport_between_count.setText(count + "");
        airport_between_addBtn = findViewById(R.id.airport_between_addBtn);
        airport_between_minusBtn = findViewById(R.id.airport_between_minusBtn);

        airport_between_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count >= 9) {
                    return;
                }
                airport_between_count.setText(String.valueOf(count));
            }
        });
        airport_between_minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 0) {
                    count--;
                    airport_between_count.setText(String.valueOf(count));
                }
            }
        });

        go_busBtn = findViewById(R.id.go_busBtn);
        go_busBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BusActivity.class);
                startActivity(intent);
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

        // 버튼 초기화
        airport_betweenBtn = findViewById(R.id.airport_betweenBtn);
        airport_onewayBtn = findViewById(R.id.airport_onewayBtn);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        TextView airportcount = findViewById(R.id.airport_between_count);

        Button oneWayButton = findViewById(R.id.airport_onewayBtn);
        final TextView showText = findViewById(R.id.airport_between_Startcalendar);
        final TextView returnText = findViewById(R.id.airport_between_Arrivecalendar);
        final ConstraintLayout layout = findViewById(R.id.airport_between_4);

        // 편도 버튼 클릭 리스너
        oneWayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRoundTrip = false; // 편도 상태 설정
                updateButtonStates();

                // ConstraintSet 초기화
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);

                // "가는 날" 버튼 위치를 가운데로 설정
                constraintSet.connect(R.id.airport_between_Startcalendar, ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
                constraintSet.connect(R.id.airport_between_Startcalendar, ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);
                constraintSet.setHorizontalBias(R.id.airport_between_Startcalendar, 0.5f);

                // "오는 날" 버튼 숨김 처리
                constraintSet.setVisibility(R.id.airport_between_Arrivecalendar, View.GONE);

                constraintSet.applyTo(layout);

                // 버튼 스타일 업데이트
                airport_betweenBtn.setBackgroundResource(R.drawable.layout_background_train3);
                airport_onewayBtn.setBackgroundResource(R.drawable.layout_background_train);

                // 텍스트 초기화
                startTextView.setText("출발지선택");
                arriveTextView.setText("도착지선택");
                selectedDateText.setText("가는 날");
                airportcount.setText("0"); // 탑승객 수 초기화
                count = 0;
            }
        });

        Button betweenButton = findViewById(R.id.airport_betweenBtn);
        final TextView showText_start = findViewById(R.id.airport_between_Startcalendar);
        final TextView showText_arrive = findViewById(R.id.airport_between_Arrivecalendar);


        // 왕복 버튼 클릭 리스너
        betweenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRoundTrip = true; // 왕복 상태 설정
                updateButtonStates();

                // ConstraintSet 초기화
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);

                // "가는 날" 버튼 왼쪽 배치
                constraintSet.connect(R.id.airport_between_Startcalendar, ConstraintSet.START, R.id.dummy, ConstraintSet.END, 0);
                constraintSet.connect(R.id.airport_between_Startcalendar, ConstraintSet.END, R.id.airport_between_Arrivecalendar, ConstraintSet.START, 0);
                constraintSet.setHorizontalBias(R.id.airport_between_Startcalendar, 0.3f);

                // "오는 날" 버튼 오른쪽 배치 및 표시
                constraintSet.setVisibility(R.id.airport_between_Arrivecalendar, View.VISIBLE);
                constraintSet.connect(R.id.airport_between_Arrivecalendar, ConstraintSet.START, R.id.airport_between_Startcalendar, ConstraintSet.END, 0);
                constraintSet.connect(R.id.airport_between_Arrivecalendar, ConstraintSet.END, R.id.btn_search, ConstraintSet.START, 0);
                constraintSet.setHorizontalBias(R.id.airport_between_Arrivecalendar, 0.7f);

                constraintSet.applyTo(layout);

                // 버튼 스타일 업데이트
                airport_betweenBtn.setBackgroundResource(R.drawable.layout_background_train);
                airport_onewayBtn.setBackgroundResource(R.drawable.layout_background_train3);

                // 텍스트 초기화
                startTextView.setText("출발지선택");
                arriveTextView.setText("도착지선택");
                selectedDateText.setText("가는 날");
                selectedDateText2.setText("오는 날");
                airportcount.setText("0"); // 탑승객 수 초기화
                count = 0;
            }
        });
        Button btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(v -> {
            // 공항 이름과 IATA 코드 매핑
            Map<String, String> airportMap = new HashMap<>();
            airportMap.put("인천국제공항", "ICN");
            airportMap.put("김포공항", "GMP");
            airportMap.put("김해공항", "PUS");
            airportMap.put("대구공항", "TAE");
            airportMap.put("괌 국제공항", "GUM");
            airportMap.put("나리타 국제공항", "NRT");
            airportMap.put("삿포로 국제공항", "CTS");
            airportMap.put("간사이 국제공항", "KIX");
            airportMap.put("오키나와 국제공항", "OKA");
            airportMap.put("후쿠오카 국제공항", "FUK");
            airportMap.put("파리 국제공항", "CDG");
            airportMap.put("베트남 국제공항", "HAN");
            airportMap.put("홍콩 국제공항", "HKG");

            // 출발지와 도착지 IATA 코드 설정
            String origin = airportMap.getOrDefault(startTextView.getText().toString().trim(), "알수없음");
            String destination = airportMap.getOrDefault(arriveTextView.getText().toString().trim(), "알수없음");

            // 출발지와 도착지 텍스트 값 가져오기
            String originKorean = startTextView.getText().toString().trim();
            String destinationKorean = arriveTextView.getText().toString().trim();

            Log.d("출발공항", "Origin: " + origin);
            Log.d("도착공항", "Destination: " + destination);

            String departureDate = selectedDateText.getText().toString();
            String returnDate = selectedDateText2.getText().toString();
            String adults = airport_between_count.getText().toString();

            // 필수 입력값 검증
            if (departureDate.isEmpty() || !departureDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                Log.e("fetchFlightData", "Invalid departure date: " + departureDate);
                Toast.makeText(this, "출발 날짜를 올바르게 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isRoundTrip && (returnDate.isEmpty() || !returnDate.matches("\\d{4}-\\d{2}-\\d{2}"))) {
                Log.e("fetchFlightData", "Invalid return date: " + returnDate);
                Toast.makeText(this, "복귀 날짜를 올바르게 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (origin.equals("알수없음") || destination.equals("알수없음")) {
                Toast.makeText(this, "출발지와 도착지를 올바르게 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (adults.isEmpty() || Integer.parseInt(adults) <= 0) {
                Toast.makeText(this, "탑승객 수는 1명 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            // 좌석 등급 검증 추가
            if (seatClass.isEmpty()) {
                Toast.makeText(this, "좌석 등급을 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 편도일 경우 복귀 날짜를 비웁니다.
            if (!isRoundTrip) {
                returnDate = "";
            }

            // API 호출
            fetchFlightData(origin, destination, departureDate, returnDate, adults);

            // 다음 액티비티로 이동
         // Intent intent = new Intent(AirportMainActivity.this, AirportResultActivity.class);
           // intent.putExtra("originKorean", originKorean);   // 출발지 텍스트 값
          //  intent.putExtra("destinationKorean", destinationKorean);  // 도착지 텍스트 값
           // intent.putExtra("airportCount",adults);
           // if (!isRoundTrip) {
            //    intent.putExtra("isRoundTrip","편도");
           // }else{
            //    intent.putExtra("isRoundTrip","왕복");
           // }
            //intent.putExtra("seatClasskr",seatClasskr);

            //startActivity(intent);

        });



        Locale locale = new Locale("ko");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        selectedDateText = findViewById(R.id.airport_between_Startcalendar);
        selectDateButton = findViewById(R.id.airport_between_Startcalendar);


        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                // 기존 출발일이 설정되어 있다면 그 날짜를 기준으로 초기화
                if (!selectedDateText.getText().toString().isEmpty() && !selectedDateText.getText().toString().equals("가는 날")) {
                    String[] parts = selectedDateText.getText().toString().split("-");
                    calendar.set(Calendar.YEAR, Integer.parseInt(parts[0]));
                    calendar.set(Calendar.MONTH, Integer.parseInt(parts[1]) - 1); // 월은 0부터 시작
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[2]));
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AirportMainActivity.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // 출발 날짜 설정
                            String departureDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                            selectedDateText.setText(departureDate);

                            // 도착 날짜의 최소 선택 가능 날짜를 업데이트
                            Calendar minReturnDate = Calendar.getInstance();
                            minReturnDate.set(selectedYear, selectedMonth, selectedDay);
                            selectDateButton2.setEnabled(true); // 도착 날짜 버튼 활성화
                            selectDateButton2.setTag(minReturnDate.getTimeInMillis()); // 최소 날짜 저장
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

                // 오늘 날짜 이후만 선택 가능하도록 설정
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

                // 도착 날짜가 이미 설정되어 있으면, 그 날짜를 최대값으로 설정
                if (!selectedDateText2.getText().toString().isEmpty() && !selectedDateText2.getText().toString().equals("오는 날")) {
                    String[] returnParts = selectedDateText2.getText().toString().split("-");
                    Calendar maxDepartureDate = Calendar.getInstance();
                    maxDepartureDate.set(Calendar.YEAR, Integer.parseInt(returnParts[0]));
                    maxDepartureDate.set(Calendar.MONTH, Integer.parseInt(returnParts[1]) - 1);
                    maxDepartureDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(returnParts[2]));
                    datePickerDialog.getDatePicker().setMaxDate(maxDepartureDate.getTimeInMillis());
                }

                datePickerDialog.show();
            }
        });





        selectedDateText2 = findViewById(R.id.airport_between_Arrivecalendar);
        selectDateButton2 = findViewById(R.id.airport_between_Arrivecalendar);
        selectDateButton2.setOnClickListener(v -> {
            if (!isRoundTrip) {
                Toast.makeText(AirportMainActivity.this, "왕복을 선택해야 복귀 날짜를 설정할 수 있습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 출발 날짜가 설정되지 않은 경우 처리
            if (selectedDateText.getText().toString().isEmpty() || selectedDateText.getText().toString().equals("가는 날")) {
                Toast.makeText(AirportMainActivity.this, "먼저 출발 날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar calendar = Calendar.getInstance();

            // 출발 날짜를 기준으로 캘린더 초기화
            String[] parts = selectedDateText.getText().toString().split("-");
            calendar.set(Calendar.YEAR, Integer.parseInt(parts[0]));
            calendar.set(Calendar.MONTH, Integer.parseInt(parts[1]) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[2]));

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AirportMainActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);

                        // 복귀 날짜가 출발 날짜 이후인지 확인
                        String[] departureParts = selectedDateText.getText().toString().split("-");
                        Calendar departureCalendar = Calendar.getInstance();
                        departureCalendar.set(Calendar.YEAR, Integer.parseInt(departureParts[0]));
                        departureCalendar.set(Calendar.MONTH, Integer.parseInt(departureParts[1]) - 1);
                        departureCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(departureParts[2]));

                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(Calendar.YEAR, selectedYear);
                        selectedCalendar.set(Calendar.MONTH, selectedMonth);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                        if (selectedCalendar.before(departureCalendar)) {
                            Toast.makeText(AirportMainActivity.this, "복귀 날짜는 출발 날짜 이후여야 합니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            selectedDateText2.setText(date);
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            // 출발 날짜 이후로 선택 가능하도록 설정
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String airportName = data.getStringExtra("airportName");
            if (requestCode == REQUEST_CODE_START) {
                // 출발지 데이터 설정
                startTextView.setText(airportName);
            } else if (requestCode == REQUEST_CODE_ARRIVE) {
                // 도착지 데이터 설정
                arriveTextView.setText(airportName);
            }
        }
    }
    private String getAccessToken() {
        try {
            String tokenUrl = "https://api.amadeus.com/v1/security/oauth2/token";
            URL url = new URL(tokenUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            String clientId = "";
            String clientSecret = "";
            String body = "grant_type=client_credentials"
                    + "&client_id=" + clientId
                    + "&client_secret=" + clientSecret;

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
                os.flush();
            }

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.getString("access_token");
            } else {
                Log.e("AirportAPITest", "토큰 요청 실패. HTTP 코드: " + conn.getResponseCode());
                return null;
            }
        } catch (Exception e) {
            Log.e("AirportAPITest", "토큰 발급 오류: " + e.getMessage());
            return null;
        }
    }
    private void fetchFlightData(String origin, String destination, String departureDate, String returnDate, String adults) {
        new Thread(() -> {
            try {
                // UI 스레드에서 요청 진행 중 메시지 표시
                runOnUiThread(() -> Toast.makeText(this, "요청을 보내는 중입니다...", Toast.LENGTH_SHORT).show());

                // 토큰 발급
                String token = getAccessToken();
                if (token == null) {
                    Log.e("fetchFlightData", "Failed to retrieve access token");
                    return;
                }

                // 편도/왕복 조건 처리
                boolean isRoundTrip = !returnDate.equals("오는 날") && !returnDate.isEmpty();

                // 요청 URL 생성
                String urlStr = "https://api.amadeus.com/v2/shopping/flight-offers?originLocationCode=" + origin
                        + "&destinationLocationCode=" + destination
                        + "&departureDate=" + departureDate
                        + (isRoundTrip ? "&returnDate=" + returnDate : "") // 왕복일 경우 복귀 날짜 추가
                        + "&adults=" + adults
                        + "&currencyCode=KRW"
                        + (seatClass.isEmpty() ? "" : "&travelClass=" + seatClass);

                Log.d("fetchFlightData", "Request URL: " + urlStr);

                // API 요청
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                // 응답 코드 확인
                int responseCode = conn.getResponseCode();
                Log.d("fetchFlightData", "Response Code: " + responseCode);

                if (responseCode == 200) {
                    // 응답 처리
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    Log.d("fetchFlightData", "Response Body: " + response.toString());


                    // JSON 파싱 및 결과 확인
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (!jsonResponse.has("data") || jsonResponse.getJSONArray("data").length() == 0) {
                        // 검색 결과가 없는 경우
                        runOnUiThread(() -> Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show());
                        return; // 화면 전환 중단
                    }



                    // 데이터를 SharedPreferences에 저장
                    getSharedPreferences("FlightData", MODE_PRIVATE)
                            .edit()
                            .clear()
                            .putString("response", response.toString())
                            .putString("origin", origin) // 출발지 저장
                            .putString("destination", destination) // 도착지 저장
                            .apply();

                    // UI 스레드에서 결과 화면으로 이동
                    runOnUiThread(() -> {
                        Intent intent = new Intent(AirportMainActivity.this, AirportResultActivity.class);
                        intent.putExtra("originKorean", startTextView.getText().toString().trim());
                        intent.putExtra("destinationKorean", arriveTextView.getText().toString().trim());
                        intent.putExtra("airportCount", adults);
                        intent.putExtra("isRoundTrip", isRoundTrip ? "왕복" : "편도");
                        intent.putExtra("seatClasskr", seatClasskr);
                        startActivity(intent);
                    });

                } else {
                    runOnUiThread(() -> Toast.makeText(this, "API 요청 실패: " + responseCode, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e("fetchFlightData", "Error: ", e);
            }
        }).start();
    }


    private void updateButtonStates() {
        if (isRoundTrip) {
            airport_betweenBtn.setBackgroundResource(R.drawable.layout_background_train);
            airport_onewayBtn.setBackgroundResource(R.drawable.layout_background_train3);
            returnText.setVisibility(View.VISIBLE);
        } else {
            airport_onewayBtn.setBackgroundResource(R.drawable.layout_background_train);
            airport_betweenBtn.setBackgroundResource(R.drawable.layout_background_train3);
            returnText.setVisibility(View.GONE);
        }
    }





}
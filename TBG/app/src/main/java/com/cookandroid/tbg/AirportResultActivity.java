package com.cookandroid.tbg;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AirportResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AirportResultAdapter adapter;
    private List<Flight> flightList;
    private TextView humanCount;
    private TextView seatClass;
    private TextView isRoundTrip;
    private String departureAirport ="";
    private String arrivalAirport="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.airport_result);

        // 뒤로가기 클릭 시 돌아가기
        Button backbtn = findViewById(R.id.backspace);
        backbtn.setOnClickListener(v -> finish());

        humanCount= findViewById(R.id.humanCount);
        isRoundTrip= findViewById(R.id.isRoundTrip);
        seatClass= findViewById(R.id.seatClass);

        recyclerView = findViewById(R.id.postsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        flightList = new ArrayList<>();
        adapter = new AirportResultAdapter(this, flightList);
        recyclerView.setAdapter(adapter);
        // Intent로 전달된 출발지와 도착지 텍스트 데이터 받기
        departureAirport = getIntent().getStringExtra("originKorean");
        arrivalAirport  = getIntent().getStringExtra("destinationKorean");


        // SharedPreferences에서 데이터 읽기
        String flightData = getSharedPreferences("FlightData", MODE_PRIVATE).getString("response", null);
        if (flightData != null) {
            parseFlightData(flightData);
        } else {
            Log.e("AirportResultActivity", "No flight data found in SharedPreferences");
            runOnUiThread(() -> Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show());
        }


        humanCount.setText(getIntent().getStringExtra("airportCount") + "명");
        isRoundTrip.setText(getIntent().getStringExtra("isRoundTrip"));
        seatClass.setText(getIntent().getStringExtra("seatClasskr"));
        //////////////////////////////////////////////////

    }

    private void parseFlightData(String flightData) {
        try {
            JSONObject responseJson = new JSONObject(flightData);
            JSONArray flights = responseJson.optJSONArray("data");
            if (flights == null || flights.length() == 0) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    flightList.clear();
                    adapter.notifyDataSetChanged();
                });
                return;
            }


            // SharedPreferences에서 출발지와 도착지 가져오기
            String origin = getSharedPreferences("FlightData", MODE_PRIVATE).getString("origin", "출발지");
            String destination = getSharedPreferences("FlightData", MODE_PRIVATE).getString("destination", "도착지");

            for (int i = 0; i < flights.length(); i++) {
                JSONObject flightJson = flights.optJSONObject(i);
                if (flightJson == null) continue;

                String price = flightJson.optJSONObject("price").optString("total", "가격 미정");

                JSONArray itineraries = flightJson.optJSONArray("itineraries");
                if (itineraries == null || itineraries.length() == 0) continue; // 최소 하나의 일정 필요

                // 출발 일정
                JSONObject departureItinerary = itineraries.optJSONObject(0);
                JSONArray departureSegments = departureItinerary.optJSONArray("segments");
                JSONObject firstSegment = departureSegments.optJSONObject(0);
                JSONObject departure = firstSegment.optJSONObject("departure");

                String departureDate = departure.optString("at").split("T")[0];
                String departureTime = formatTime(departure.optString("at").split("T")[1]); // 시간 형식 변환
                String departureAirlineCode = firstSegment.optString("carrierCode");
                String departureAirlineName = getAirlineName(departureAirlineCode);

                // 복귀 일정 (편도일 경우 데이터 없음 처리)
                String returnDate = "";
                String returnTime = "";
                String returnAirlineCode = "";
                String returnAirlineName = "";

                if (itineraries.length() > 1) {
                    JSONObject returnItinerary = itineraries.optJSONObject(1);
                    JSONArray returnSegments = returnItinerary.optJSONArray("segments");
                    JSONObject returnSegment = returnSegments.optJSONObject(0);
                    JSONObject returnDeparture = returnSegment.optJSONObject("departure");

                    returnDate = returnDeparture.optString("at").split("T")[0];
                    returnTime = formatTime(returnDeparture.optString("at").split("T")[1]); // 시간 형식 변환
                    returnAirlineCode = returnSegment.optString("carrierCode");
                    returnAirlineName = getAirlineName(returnAirlineCode);
                }
                DecimalFormat formatter = new DecimalFormat("#,###");
                // 가격 포맷팅
                double formattedPrice = Double.parseDouble(price);
                String formattedPriceStr = formatter.format(formattedPrice);


                // 포맷된 가격과 공항 정보를 flightList에 추가
                flightList.add(new Flight(
                        origin + " - " + destination, // 제목
                        departureDate,                // 출발 날짜
                        departureTime,                // 출발 시간
                        returnDate,                   // 복귀 날짜 (왕복일 경우)
                        returnTime,                   // 복귀 시간 (왕복일 경우)
                        formattedPriceStr,            // 포맷된 가격
                        departureAirlineCode,         // 출발 항공사 코드
                        returnAirlineCode,            // 복귀 항공사 코드
                        departureAirlineName,         // 출발 항공사 이름
                        returnAirlineName,            // 복귀 항공사 이름
                        departureAirport,             // 출발 공항
                        arrivalAirport               // 도착 공항
                ));

            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("parseFlightData", "Error parsing flight data", e);
        }
    }
    private String formatTime(String time) {
        try {
            // JSON 데이터의 시간 형식: HH:mm:ss (예: "23:50:00")
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            return outputFormat.format(inputFormat.parse(time));
        } catch (Exception e) {
            e.printStackTrace();
            return time; // 변환 실패 시 원래 값을 반환
        }
    }

    private String getAirlineName(String airlineCode) {
        // 항공사 코드에 따른 이름 매핑 (예시)
        switch (airlineCode) {
            case "KE":
                return "대한항공";
            case "OZ":
                return "아시아나항공";
            case "TW":
                return "티웨이항공";
            case "7C":
                return "제주항공";
            case "AA":
                return "아메리칸 항공";
            case "UA":
                return "유나이티드 항공";
            case "DL":
                return "델타 항공";
            case "AF":
                return "에어프랑스";
            case "LH":
                return "독일항공";
            case "BA":
                return "영국항공";
            case "CX":
                return "캐세이퍼시픽 항공";
            case "QF":
                return "퀸즐랜드 항공";
            case "SQ":
                return "싱가포르 항공";
            case "NZ":
                return "뉴질랜드 항공";
            case "EK":
                return "에미레이트 항공";
            case "QR":
                return "카타르 항공";
            case "JL":
                return "일본항공";
            case "NH":
                return "전일본공수";
            case "SU":
                return "러시아항공";
            case "SA":
                return "사우스아프리카 항공";
            case "SK":
                return "스칸디나비아 항공";
            case "IB":
                return "이베리아 항공";
            case "AC":
                return "에어캐나다";
            case "MS":
                return "이집트항공";
            case "MU":
                return "중국동방항공";
            case "HU":
                return "허난항공";
            case "FM":
                return "상하이항공";
            case "CA":
                return "중국국제항공";
            case "9W":
                return "제트항공";
            case "VY":
                return "발렌시아 항공";
            case "WY":
                return "오만 항공";
            case "HG":
                return "헝가리 항공";
            case "B6":
                return "제트블루 항공";
            case "F9":
                return "프런티어 항공";
            case "VX":
                return "버지니아 항공";
            case "AS":
                return "알래스카 항공";
            case "LY":
                return "엘알 항공";
            case "TK":
                return "터키항공";
            case "VA":
                return "버진 오스트레일리아";
            case "CI":
                return "중화항공";
            case "OK":
                return "체코항공";
            case "AI":
                return "인도항공";
            case "GA":
                return "가루다 인도네시아 항공";
            case "AT":
                return "로얄 에어 모로코";
            case "AM":
                return "아에로멕시코";
            case "A3":
                return "에이즈 항공";
            case "LX":
                return "스위스 항공";
            case "TG":
                return "타이항공";
            case "H7":
                return "허비어항공";
            case "F7":
                return "파이브스타 항공";
            case "UB":
                return "우즈베키스탄 항공";
            case "V8":
                return "버진 항공";
            case "G9":
                return "와이비항공";
            case "M8":
                return "중앙항공";
            case "PZ":
                return "포르투갈 항공";
            case "RL":
                return "레벨 항공";
            case "Z3":
                return "제트스타 항공";
            case "WG":
                return "웨스트젯";
            case "V7":
                return "비게이 항공";
            case "I5":
                return "인디고 항공";
            case "JT":
                return "자이로항공";
            case "XJ":
                return "타이 항공";
            case "VE":
                return "바이리타 항공";
            case "U6":
                return "우크라이나 항공";
            case "H6":
                return "하이멕스 항공";
            case "V3":
                return "비앙키 항공";
            case "H8":
                return "헬레니 항공";
            case "Q3":
                return "큐브항공";
            case "R3":
                return "라트비아 항공";
            case "S7":
                return "시베리아 항공";
            case "E7":
                return "에어 아스타나";
            case "QX":
                return "퀴비 항공";
            case "V1":
                return "베트남 항공";
            case "Z9":
                return "제트날리 항공";
            case "YV":
                return "옐로우 항공";
            case "V2":
                return "블루 항공";
            case "T5":
                return "타이거 항공";
            case "R5":
                return "로얄 항공";
            case "C9":
                return "쿠바 항공";
            case "S9":
                return "스카이하이 항공";
            case "B3":
                return "벨리즈 항공";
            case "G7":
                return "로이 항공";
            case "F8":
                return "프리미엄 항공";
            case "D9":
                return "두바이 항공";
            case "N4":
                return "나이지리아 항공";
            case "O6":
                return "오르노 항공";
            case "Q5":
                return "퀘스트 항공";
            case "KQ":
                return "케냐항공";
            case "JQ":
                return "제트스타 항공";
            case "C4":
                return "쿠알라룸푸르 항공";
            case "M9":
                return "마카오 항공";
            case "N6":
                return "뉴질랜드 국적 항공";
            case "MF":
                return "샤먼 항공";
            default:
                return "충환 항공";
        }


    }
}

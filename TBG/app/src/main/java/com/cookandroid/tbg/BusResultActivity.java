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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BusResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BusResultAdapter adapter;
    private List<Bus> busList;

    private String departureStation = "";
    private String arrivalStation = "";
    private String humanCount = "";
    private String busData = "";
    private String startTime = "";
    private int count;
    private TextView humanCountset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_result);


        // 뒤로가기 클릭 시 돌아가기
        Button backbtn = findViewById(R.id.backspace);
        backbtn.setOnClickListener(v -> finish());


        recyclerView = findViewById(R.id.postsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        busList = new ArrayList<>();
        adapter = new BusResultAdapter(this, busList);
        recyclerView.setAdapter(adapter);

        // Intent로 전달된 출발지와 도착지 텍스트 데이터 받기
        departureStation = getIntent().getStringExtra("startTextkr");
        arrivalStation = getIntent().getStringExtra("arriveTextkr");
        humanCount = getIntent().getStringExtra("humanCount");
        startTime = getIntent().getStringExtra("startTime");
        count = getIntent().getIntExtra("count", 1);
        humanCountset = findViewById(R.id.humanCount);
        humanCountset.setText(count + "명");

        // SharedPreferences에서 데이터 읽기
        busData = getSharedPreferences("BusData", MODE_PRIVATE).getString("response", null);
        Log.d("BusResultActivity", "Stored bus data: " + busData);

        if (busData != null) {
            Log.d("BusResultActivity", "데이터 잘옴");
            parseBusData(busData);
        } else {
            Log.d("BusResultActivity", "데이터 제대로 안옴");
            showToast("검색 결과가 없습니다.");
        }
    }

    private void parseBusData(String busData) {
        try {
            JSONObject responseJson = new JSONObject(busData);
            JSONObject body = responseJson.optJSONObject("body");
            if (body == null) {
                Log.e("parseBusData", "Body object is null");
                showToast("데이터가 없습니다.");
                return;
            }

            Object items = body.opt("items");
            if (items instanceof String && ((String) items).isEmpty()) {
                Log.e("parseBusData", "Items is an empty string.");
                showToast("검색된 버스 정보가 없습니다.");
                return;
            }

            JSONArray buses = null;
            if (items instanceof JSONObject) {
                buses = ((JSONObject) items).optJSONArray("item");
            } else if (items instanceof JSONArray) {
                buses = (JSONArray) items;
            }

            if (buses == null || buses.length() == 0) {
                Log.e("parseBusData", "Bus array is empty");
                showToast("검색된 버스 정보가 없습니다.");
                return;
            }

            for (int i = 0; i < buses.length(); i++) {
                JSONObject busJson = buses.optJSONObject(i);
                if (busJson == null) continue;

                Bus bus = new Bus();
                bus.setDepartureStation(busJson.optString("depPlaceNm", departureStation));
                bus.setArrivalStation(busJson.optString("arrPlaceNm", arrivalStation));
                bus.setArrivalDate(formatDate(busJson.optString("arrPlandTime", "N/A")));
                bus.setDepartureDate(formatDate(busJson.optString("depPlandTime", "N/A")));
                bus.setDepartureTime(formatTime(busJson.optString("depPlandTime", "N/A")));
                bus.setArrivalTime(formatTime(busJson.optString("arrPlandTime", "N/A")));
                bus.setPrice(String.valueOf(busJson.optInt("charge", 0) * count));
                bus.setSeatClass(busJson.optString("gradeNm", "N/A")); //이게문제
                
                Log.d("ㅇㅇㅇㅇㅇㅇ",busJson.optString("gradeNm", "N/A") );

                busList.add(bus);
                Log.d("ㅇㅇ", "잘저장됨");
            }

            runOnUiThread(adapter::notifyDataSetChanged);
        } catch (Exception e) {
            Log.e("parseBusData", "Error parsing bus data", e);
            showToast("데이터 처리 중 오류가 발생했습니다.");
        }
    }


    private String formatDate(String dateTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = inputFormat.parse(dateTime);
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            Log.e("에러22", e.toString());
            e.printStackTrace();
            return dateTime;
        }
    }

    private String formatTime(String dateTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date parsedDate = inputFormat.parse(dateTime);
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }
}
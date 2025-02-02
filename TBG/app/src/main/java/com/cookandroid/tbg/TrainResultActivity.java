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

public class TrainResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TrainResultAdapter adapter;
    private List<Train> trainList;

    private String departureStation = "";
    private String arrivalStation = "";
    private String humanCount = "";
    private String trainData = "";
    private String startTime = "";
    private TextView hCount;
    private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_result);

        // 뒤로가기 클릭 시 돌아가기
        Button backbtn = findViewById(R.id.backspace);
        backbtn.setOnClickListener(v -> finish());

        hCount = findViewById(R.id.humanCount);
        recyclerView = findViewById(R.id.postsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        trainList = new ArrayList<>();
        adapter = new TrainResultAdapter(trainList, this);
        recyclerView.setAdapter(adapter);

        // Intent로 전달된 출발지와 도착지 텍스트 데이터 받기
        departureStation = getIntent().getStringExtra("startTextkr");
        arrivalStation = getIntent().getStringExtra("arriveTextkr");
        humanCount = getIntent().getStringExtra("humanCount");
        startTime = getIntent().getStringExtra("startTime");
        count = getIntent().getIntExtra("count", 1);//인원수를 안받으면 1명으로 계산하기

        hCount.setText(humanCount + "명");

        // SharedPreferences에서 데이터 읽기
        trainData = getSharedPreferences("TrainData", MODE_PRIVATE).getString("response", null);
        Log.d("TrainResultActivity", "Stored train data: " + trainData);

        if (trainData != null) {
            Log.d("TrainResultActivity", "데이터 잘옴");
            parseTrainData(trainData);
        } else {
            Log.d("TrainResultActivity", "데이터 제대로 안옴");
            showToast("검색 결과가 없습니다.");
        }
    }

    private void parseTrainData(String trainData) {
        try {
            // JSON 구조 확인 및 최상위 객체 생성
            JSONObject responseJson = new JSONObject(trainData); // 전체 데이터 파싱

            // 'header' 확인 (optional, 필요시 로직 추가 가능)
            JSONObject header = responseJson.optJSONObject("header");
            if (header != null) {
                Log.d("parseTrainData", "Header: " + header.toString());
            }

            // 'body' 확인
            JSONObject body = responseJson.optJSONObject("body");
            if (body == null) {
                Log.e("parseTrainData", "Body object is null");
                showToast("데이터가 없습니다.");
                return;
            }
            Log.d("parseTrainData", "Body object: " + body.toString());

            // 'items' 확인
            JSONObject items = body.optJSONObject("items");
            if (items == null) {
                Log.e("parseTrainData", "Items object is null");
                showToast("검색 결과가 없습니다.");
                return;
            }
            Log.d("parseTrainData", "Items object: " + items.toString());

            // 'item' 배열 확인
            JSONArray trains = items.optJSONArray("item");
            if (trains == null || trains.length() == 0) {
                Log.e("parseTrainData", "Trains array is empty");
                showToast("검색된 열차 정보가 없습니다.");
                return;
            }
            Log.d("parseTrainData", "Trains array: " + trains.toString());

            // JSON 데이터 파싱
            for (int i = 0; i < trains.length(); i++) {
                JSONObject trainJson = trains.optJSONObject(i);
                if (trainJson == null) continue;

                Log.d("테스트" + i ,trainJson.optString("trainno", "N/A") );
                Log.d("테스트" + i ,trainJson.optString("depplacename", departureStation) );


                // Train 객체 생성 및 데이터 설정
                Train train = new Train();
                train.setTrainNo(trainJson.optString("trainno", "N/A"));
                train.setDepartureStation(trainJson.optString("depplacename", departureStation));
                train.setArrivalStation(trainJson.optString("arrplacename", arrivalStation));
                train.setDepartureTime(formatTime(trainJson.optString("depplandtime", "N/A")));

                Log.d("출발시간 : ",formatTime(trainJson.optString("depplandtime", "N/A")));

                train.setArrivalTime(formatTime(trainJson.optString("arrplandtime", "N/A")));
                train.setDepartureDate(formatDate(trainJson.optString("depplandtime","N/A")));
                train.setArrivalDate(formatDate(trainJson.optString("arrplandtime","N/A")));
                train.setTrainType(trainJson.optString("traingradename", "N/A"));
                train.setPrice(String.valueOf(trainJson.optInt("adultcharge", 0) * count));
                // 리스트에 추가
                trainList.add(train);
            }


            // RecyclerView 갱신
            runOnUiThread(adapter::notifyDataSetChanged);
        } catch (Exception e) {
            Log.e("parseTrainData", "Error parsing train data", e);
            showToast("데이터 처리 중 오류가 발생했습니다.");
        }
    }

    // 날짜를 "yyyy-MM-dd" 형식으로 변환
    private String formatDate(String dateTime) {
        try {
            // 입력 형식: yyyyMMddHHmmss
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            // 출력 형식: yyyy-MM-dd
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            // 파싱 후 변환
            Date parsedDate = inputFormat.parse(dateTime);
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            Log.e("formatDateError", "Error parsing date: " + dateTime, e);
            return dateTime; // 변환 실패 시 원래 값을 반환
        }
    }

    // 시간을 "HH:mm" 형식으로 변환
    private String formatTime(String dateTime) {
        try {
            // 입력 형식: yyyyMMddHHmmss
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            // 출력 형식: HH:mm
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            // 파싱 후 변환
            Date parsedDate = inputFormat.parse(dateTime);
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            Log.e("formatTimeError", "Error parsing time: " + dateTime, e);
            return dateTime; // 변환 실패 시 원래 값을 반환
        }
    }



    // UI 스레드에서 Toast 메시지 출력
    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }
}

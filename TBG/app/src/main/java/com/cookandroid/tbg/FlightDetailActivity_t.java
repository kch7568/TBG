package com.cookandroid.tbg;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FlightDetailActivity_t extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_detail);

        // UI 초기화
        TextView tvPrice = findViewById(R.id.tv_detail_price);
        TextView tvDepartureInfo = findViewById(R.id.tv_detail_departure);
        TextView tvArrivalInfo = findViewById(R.id.tv_detail_arrival);
        TextView tvReturnInfo = findViewById(R.id.tv_detail_return);

        // Intent로부터 데이터 가져오기
        String price = getIntent().getStringExtra("price");
        String departureInfo = getIntent().getStringExtra("departureInfo");
        String arrivalInfo = getIntent().getStringExtra("arrivalInfo");
        String returnInfo = getIntent().getStringExtra("returnInfo");

        // 데이터 설정
        tvPrice.setText("가격: " + price);
        tvDepartureInfo.setText("출발 정보: " + departureInfo);
        tvArrivalInfo.setText("도착 정보: " + arrivalInfo);

        if (returnInfo != null && !returnInfo.isEmpty()) {
            tvReturnInfo.setText("복귀 정보: " + returnInfo);
        } else {
            tvReturnInfo.setText("복귀 정보: 없음");
        }
    }
}

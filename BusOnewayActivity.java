package com.example.tbg;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;

public class BusOnewayActivity extends AppCompatActivity {

    Button btn1;


    private Button bus_oneway_addBtn, bus_oneway_minusBtn;
    private TextView bus_oneway_count;
    private int count = 0;

    ImageView go_trainBtn;
    ImageView go_airportBtn;

    private TextView selectedDateText;
    private Button selectDateButton;

    private static final int REQUEST_CODE_START = 1; // 출발지 요청 코드
    private static final int REQUEST_CODE_ARRIVE = 2; // 도착지 요청 코드

    private TextView startTextView3;
    private TextView arriveTextView3;

    @SuppressLint({"MissingInflatedId", "SetTextI18n", "WrongViewCast", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.bus_oneway);

        startTextView3 = findViewById(R.id.start_bus);
        arriveTextView3 = findViewById(R.id.arrive_bus);

        // 출발지를 설정하기 위한 클릭 이벤트
        startTextView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PracticeActivity를 실행하여 결과를 받음
                Intent intent = new Intent(BusOnewayActivity.this, BusOnewayStartActivity.class);
                startActivityForResult(intent, REQUEST_CODE_START);
            }
        });

        // 도착지를 설정하기 위한 클릭 이벤트
        arriveTextView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PracticeActivity를 실행하여 결과를 받음
                Intent intent = new Intent(BusOnewayActivity.this, BusOnewayArriveActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ARRIVE);
            }
        });



        btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BusBetweenActivity.class);
                startActivity(intent);
            }
        });

        bus_oneway_count = findViewById(R.id.bus_oneway_count);
        bus_oneway_count.setText(count + "");
        bus_oneway_addBtn = findViewById(R.id.bus_oneway_addBtn);
        bus_oneway_minusBtn = findViewById(R.id.bus_oneway_minusBtn);

        bus_oneway_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                bus_oneway_count.setText(count + "");
            }
        });

        bus_oneway_minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                bus_oneway_count.setText(count + "");
            }
        });

        go_airportBtn = findViewById(R.id.go_airportBtn);
        go_airportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AirportOnewayActivity.class);
                startActivity(intent);
            }
        });

        go_trainBtn = findViewById(R.id.go_trainBtn);
        go_trainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainOnewayActivity.class);
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
                        BusOnewayActivity.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // 선택된 날짜를 TextView에 표시
                            String date = (selectedMonth + 1) + "월" + " / " + selectedDay + "일";
                            selectedDateText.setText(date);
                        },
                        year, month, day
                );

                datePickerDialog.show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            String busName = data.getStringExtra("busName");

            if (requestCode == REQUEST_CODE_START) {
                // 출발지 데이터 설정
                startTextView3.setText("출발역 : " + busName);
            } else if (requestCode == REQUEST_CODE_ARRIVE) {
                // 도착지 데이터 설정
                arriveTextView3.setText("도착역 : " + busName);
            }
        }
    }
}
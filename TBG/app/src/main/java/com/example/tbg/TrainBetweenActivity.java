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

public class TrainBetweenActivity extends AppCompatActivity {

    private Button train_between_addBtn, train_between_minusBtn;
    private TextView train_between_tv_count;
    private int count = 0;

    ImageView go_busBtn;
    ImageView go_airportBtn;

    TextView train_between_startDay;
    TextView train_between_arriveDay;

    TextView train_go_onewayBtn;

    private TextView selectedDateText;
    private Button selectDateButton;

    private TextView selectedDateText2;
    private Button selectDateButton2;

    private static final int REQUEST_CODE_START = 1; // 출발지 요청 코드
    private static final int REQUEST_CODE_ARRIVE = 2; // 도착지 요청 코드

    private TextView startTextView;
    private TextView arriveTextView;

    @SuppressLint({"MissingInflatedId", "SetTextI18n", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.train_between);

        startTextView = findViewById(R.id.start_Place2);
        arriveTextView = findViewById(R.id.arrive_Place2);

        // 출발지를 설정하기 위한 클릭 이벤트
        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PracticeActivity를 실행하여 결과를 받음
                Intent intent = new Intent(TrainBetweenActivity.this, TrainBetweenStartActivity.class);
                startActivityForResult(intent, REQUEST_CODE_START);
            }
        });

        // 도착지를 설정하기 위한 클릭 이벤트
        arriveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PracticeActivity를 실행하여 결과를 받음
                Intent intent = new Intent(TrainBetweenActivity.this, TrainBetweenArriveActivity.class);
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
                count++;
                train_between_tv_count.setText(count + "");
            }
        });

        train_between_minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                train_between_tv_count.setText(count + "");
            }
        });

        train_between_startDay = findViewById(R.id.train_between_startDay);
        train_between_startDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainBetweenStartActivity.class);
                startActivity(intent);
            }
        });

        train_between_arriveDay = findViewById(R.id.train_between_arriveDay);
        train_between_arriveDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainBetweenArriveActivity.class);
                startActivity(intent);
            }
        });

        train_go_onewayBtn = findViewById(R.id.train_go_onewayBtn);
        train_go_onewayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainOnewayActivity.class);
                startActivity(intent);
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

        go_busBtn = findViewById(R.id.go_busBtn);
        go_busBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BusOnewayActivity.class);
                startActivity(intent);
            }
        });

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
                        TrainBetweenActivity.this,
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

        selectedDateText2 = findViewById(R.id.train_between_Arrivecalendar);
        selectDateButton2 = findViewById(R.id.train_between_Arrivecalendar);

        selectDateButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 날짜를 기준으로 설정
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // DatePickerDialog 생성
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TrainBetweenActivity.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // 선택된 날짜를 TextView에 표시
                            String date = (selectedMonth + 1) + "월" + " / " + selectedDay + "일";
                            selectedDateText2.setText(date);
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
                String stationName2 = data.getStringExtra("stationName2");

                if (requestCode == REQUEST_CODE_START) {
                    // 출발지 데이터 설정
                    startTextView.setText("출발역 : " + stationName2);
                } else if (requestCode == REQUEST_CODE_ARRIVE) {
                    // 도착지 데이터 설정
                    arriveTextView.setText("도착역 : " + stationName2);
                }
            }
    }
}
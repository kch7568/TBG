package com.example.tbg;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;

public class AirportBetweenActivity extends AppCompatActivity {

    private Button airport_between_addBtn, airport_between_minusBtn;
    private TextView airport_between_count;
    private int count = 0;

    Button airport_go_onewayBtn;

    ImageView go_trainBtn;
    ImageView go_busBtn;

    private TextView selectedDateText;
    private Button selectDateButton;

    private TextView selectedDateText2;
    private Button selectDateButton2;

    private static final int REQUEST_CODE_START = 1; // 출발지 요청 코드
    private static final int REQUEST_CODE_ARRIVE = 2; // 도착지 요청 코드

    private TextView startTextView4;
    private TextView arriveTextView4;


    @SuppressLint({"MissingInflatedId", "SetTextI18n", "WrongViewCast", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.airport_between);

        startTextView4 = findViewById(R.id.start_airport);
        arriveTextView4 = findViewById(R.id.arrive_airport);

        // 출발지를 설정하기 위한 클릭 이벤트
        startTextView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PracticeActivity를 실행하여 결과를 받음
                Intent intent = new Intent(AirportBetweenActivity.this, AirportBetweenStartActivity.class);
                startActivityForResult(intent, REQUEST_CODE_START);
            }
        });

        // 도착지를 설정하기 위한 클릭 이벤트
        arriveTextView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PracticeActivity를 실행하여 결과를 받음
                Intent intent = new Intent(AirportBetweenActivity.this, AirportBetweenArriveActivity.class);
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
                airport_between_count.setText(count + "");
            }
        });

        airport_between_minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                airport_between_count.setText(count + "");
            }
        });


        airport_go_onewayBtn = findViewById(R.id.airport_go_onewayBtn);
        airport_go_onewayBtn.setOnClickListener(new View.OnClickListener() {
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

        go_trainBtn = findViewById(R.id.go_trainBtn);
        go_trainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainOnewayActivity.class);
                startActivity(intent);
            }
        });

        selectedDateText = findViewById(R.id.airport_between_Startcalendar);
        selectDateButton = findViewById(R.id.airport_between_Startcalendar);

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
                        AirportBetweenActivity.this,
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

        selectedDateText2 = findViewById(R.id.airport_between_Arrivecalendar);
        selectDateButton2 = findViewById(R.id.airport_between_Arrivecalendar);

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
                        AirportBetweenActivity.this,
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
            String airportName = data.getStringExtra("airportName");

            if (requestCode == REQUEST_CODE_START) {
                // 출발지 데이터 설정
                startTextView4.setText("출발역 : " + airportName);
            } else if (requestCode == REQUEST_CODE_ARRIVE) {
                // 도착지 데이터 설정
                arriveTextView4.setText("도착역 : " + airportName);
            }
        }
    }
}

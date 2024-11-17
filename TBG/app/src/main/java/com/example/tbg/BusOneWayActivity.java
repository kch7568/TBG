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

    Button bus_oneway_startDay;
    Button bus_oneway_arriveDay;

    private Button bus_oneway_addBtn, bus_oneway_minusBtn;
    private TextView bus_oneway_count;
    private int count = 0;

    ImageView go_trainBtn;
    ImageView go_airportBtn;

    private TextView selectedDateText;
    private Button selectDateButton;

    @SuppressLint({"MissingInflatedId", "SetTextI18n", "WrongViewCast", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.bus_oneway);

        bus_oneway_startDay = findViewById(R.id.bus_oneway_startDay);
        bus_oneway_startDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BusOnewayStartActivity.class);
                startActivity(intent);
            }
        });

        bus_oneway_arriveDay = findViewById(R.id.bus_oneway_arriveDay);
        bus_oneway_arriveDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BusOnewayArriveActivity.class);
                startActivity(intent);
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
}
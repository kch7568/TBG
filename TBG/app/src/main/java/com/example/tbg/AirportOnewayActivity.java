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

public class AirportOnewayActivity extends AppCompatActivity {

    Button btn1;

    Button airport_oneway_startDay;
    Button airport_oneway_arriveDay;

    private Button airport_oneway_addBtn, airport_oneway_minusBtn;
    private TextView airport_oneway_count;
    private int count = 0;

    ImageView go_trainBtn;
    ImageView go_busBtn;

    private TextView selectedDateText;
    private Button selectDateButton;

    @SuppressLint({"MissingInflatedId", "SetTextI18n", "WrongViewCast", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.airport_oneway);

        btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AirportBetweenActivity.class);
                startActivity(intent);
            }
        });

        airport_oneway_count = findViewById(R.id.airport_oneway_count);
        airport_oneway_count.setText(count + "");
        airport_oneway_addBtn = findViewById(R.id.airport_oneway_addBtn);
        airport_oneway_minusBtn = findViewById(R.id.airport_oneway_minusBtn);

        airport_oneway_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                airport_oneway_count.setText(count + "");
            }
        });

        airport_oneway_minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                airport_oneway_count.setText(count + "");
            }
        });

        airport_oneway_startDay = findViewById(R.id.airport_oneway_startDay);
        airport_oneway_startDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        airport_oneway_arriveDay = findViewById(R.id.airport_oneway_arriveDay);
        airport_oneway_arriveDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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

        selectedDateText = findViewById(R.id.airport_Startcalendar);
        selectDateButton = findViewById(R.id.airport_Startcalendar);

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
                        AirportOnewayActivity.this,
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
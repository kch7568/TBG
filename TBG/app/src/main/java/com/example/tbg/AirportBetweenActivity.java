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

    Button airport_between_startDay;
    Button airport_between_arriveDay;

    Button airport_go_onewayBtn;

    ImageView go_trainBtn;
    ImageView go_busBtn;

    private TextView selectedDateText;
    private Button selectDateButton;

    private TextView selectedDateText2;
    private Button selectDateButton2;


    @SuppressLint({"MissingInflatedId", "SetTextI18n", "WrongViewCast", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.airport_between);

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

        airport_between_startDay = findViewById(R.id.airport_between_startDay);
        airport_between_startDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        airport_between_arriveDay = findViewById(R.id.airport_between_arriveDay);
        airport_between_arriveDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
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
}

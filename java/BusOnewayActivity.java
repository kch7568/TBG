package com.example.tbg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

public class BusOnewayActivity extends AppCompatActivity {

    Button btn1;

    Button bus_oneway_startDay;
    Button bus_oneway_arriveDay;

    private Button bus_oneway_addBtn, bus_oneway_minusBtn;
    private TextView bus_oneway_count;
    private int count = 0;

    ImageView go_trainBtn;
    ImageView go_airportBtn;

    TextView selectedDate;
    Button calendarButton;

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

        selectedDate=findViewById(R.id.bus_Startcalendar);
        calendarButton = findViewById(R.id.bus_Startcalendar);
        MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText("select Date").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();

        calendarButton.setOnClickListener(new View.OnClickListener() { //날짜 선택기가 열림
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(), "Tag_Picker");
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        selectedDate.setText(materialDatePicker.getHeaderText());
                    }
                });
            }
        });

    }
}
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

public class BusBetweenActivity extends AppCompatActivity {

    private Button bus_between_addBtn, bus_between_minusBtn;
    private TextView bus_between_count;
    private int count = 0;

    Button bus_between_startDay;
    Button bus_between_arriveDay;

    Button bus_go_onewayBtn;

    ImageView go_trainBtn;
    ImageView go_airportBtn;

    TextView selectedDate;
    Button calendarButton;

    @SuppressLint({"MissingInflatedId", "SetTextI18n", "WrongViewCast", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.bus_between);

        bus_between_count = findViewById(R.id.bus_between_count);
        bus_between_count.setText(count + "");
        bus_between_addBtn = findViewById(R.id.bus_between_addBtn);
        bus_between_minusBtn = findViewById(R.id.bus_between_minusBtn);

        bus_between_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                bus_between_count.setText(count + "");
            }
        });

        bus_between_minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                bus_between_count.setText(count + "");
            }
        });

        bus_between_startDay = findViewById(R.id.bus_between_startDay);
        bus_between_startDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        bus_between_arriveDay = findViewById(R.id.bus_between_arriveDay);
        bus_between_arriveDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        bus_go_onewayBtn = findViewById(R.id.bus_go_onewayBtn);
        bus_go_onewayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BusOnewayActivity.class);
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

        go_trainBtn = findViewById(R.id.go_trainBtn);
        go_trainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainOnewayActivity.class);
                startActivity(intent);
            }
        });

        selectedDate=findViewById(R.id.bus_between_startcalendar);
        calendarButton = findViewById(R.id.bus_between_startcalendar);
        MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText("select Date").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();

        calendarButton.setOnClickListener(new View.OnClickListener() {
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
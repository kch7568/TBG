package com.example.tbg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

public class TrainOnewayActivity extends AppCompatActivity {

    Button btn1;

    Button train_oneway_startDay;
    Button train_oneway_arriveDay;

    private Button train_oneway_addBtn, train_oneway_minusBtn;
    private TextView train_oneway_count;
    private int count = 0;

    ImageView go_busBtn;
    ImageView go_airportBtn;

    TextView selectedDate;
    Button calendarButton;

    @SuppressLint({"MissingInflatedId", "SetTextI18n", "WrongViewCast", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.train_oneway);




        train_oneway_count = findViewById(R.id.train_oneway_count);
        train_oneway_count.setText(count + "");
        train_oneway_addBtn = findViewById(R.id.train_oneway_addBtn);
        train_oneway_minusBtn = findViewById(R.id.train_oneway_minusBtn);

        train_oneway_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                train_oneway_count.setText(count + "");
            }
        });

        train_oneway_minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                train_oneway_count.setText(count + "");
            }
        });


        train_oneway_startDay = findViewById(R.id.train_oneway_startDay);
        train_oneway_startDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainOnewayStartActivity.class);
                startActivity(intent);
            }
        });

        train_oneway_arriveDay = findViewById(R.id.train_oneway_arriveDay);
        train_oneway_arriveDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainOnewayArriveActivity.class);
                startActivity(intent);
            }
        });

        btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainBetweenActivity.class);
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

        selectedDate=findViewById(R.id.train_Startcalendar);
        calendarButton = findViewById(R.id.train_Startcalendar);
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




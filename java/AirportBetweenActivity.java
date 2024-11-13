package com.example.tbg;

import android.annotation.SuppressLint;
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

public class AirportBetweenActivity extends AppCompatActivity {

    private Button airport_between_addBtn, airport_between_minusBtn;
    private TextView airport_between_count;
    private int count = 0;

    Button airport_between_startDay;
    Button airport_between_arriveDay;

    Button airport_go_onewayBtn;

    ImageView go_trainBtn;
    ImageView go_busBtn;

    TextView selectedDate;
    Button calendarButton;

    TextView selectedDate2;
    Button calendarButton2;


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

        selectedDate = findViewById(R.id.airport_between_Startcalendar);
        calendarButton = findViewById(R.id.airport_between_Startcalendar);
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

        selectedDate2=findViewById(R.id.airport_between_Arrivecalendar);
        calendarButton2 = findViewById(R.id.airport_between_Arrivecalendar);
        MaterialDatePicker materialDatePicker2 = MaterialDatePicker.Builder.datePicker().setTitleText("select Date").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();

        calendarButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker2.show(getSupportFragmentManager(), "Tag_Picker2");
                materialDatePicker2.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        selectedDate2.setText(materialDatePicker2.getHeaderText());
                    }
                });
            }
        });
    }
}

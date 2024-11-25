package com.example.tbg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Railroad2CalendarActivity extends AppCompatActivity {

    CalendarView Cal2;
    TextView text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.railroad2_calendar);

        Cal2 = findViewById(R.id.Cal2);
        text2 = findViewById(R.id.text2);

        Cal2.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                text2.setText( month + "월" + day + "일");
            }
        });

        Button btnEnd2 = (Button) findViewById(R.id.btnEnd2);
        btnEnd2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
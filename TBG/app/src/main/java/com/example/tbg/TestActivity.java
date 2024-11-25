package com.example.tbg;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Locale;
import android.content.res.Configuration;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = new Locale("ko");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        setContentView(R.layout.test);

        CalendarView calendarView = findViewById(R.id.calendarView);
        TextView selectedDateTextView = findViewById(R.id.selectedDateTextView);

        // 오늘 날짜 가져오기
        Calendar calendar = Calendar.getInstance();

        // CalendarView의 최소 날짜를 오늘 날짜로 설정
        calendarView.setMinDate(calendar.getTimeInMillis());


        // 날짜 선택 이벤트 처리
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = (month + 1) + "-" + dayOfMonth;
            selectedDateTextView.setText("선택된 날짜: " + selectedDate);
        });

    }
}
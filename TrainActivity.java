package com.example.tbg;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.Calendar;
import java.util.Locale;

public class TrainActivity extends AppCompatActivity {

    private Button train_between_addBtn, train_between_minusBtn;
    private TextView train_between_tv_count;
    private int count = 0;

    ImageView go_busBtn;
    ImageView go_airportBtn;

    Button train_betweenBtn, train_onewayBtn;

    private TextView selectedDateText;
    private Button selectDateButton;

    private TextView selectedDateText2;
    private Button selectDateButton2;

    private static final int REQUEST_CODE_START = 1; // 출발지 요청 코드
    private static final int REQUEST_CODE_ARRIVE = 2; // 도착지 요청 코드

    private TextView startTextView;
    private TextView arriveTextView;

    @SuppressLint({"MissingInflatedId", "SetTextI18n", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.train);

        startTextView = findViewById(R.id.start_Place2);
        arriveTextView = findViewById(R.id.arrive_Place2);

        // 출발지를 설정하기 위한 클릭 이벤트
        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PracticeActivity를 실행하여 결과를 받음
                Intent intent = new Intent(TrainActivity.this, TrainStartPlaceActivity.class);
                startActivityForResult(intent, REQUEST_CODE_START);
            }
        });

        // 도착지를 설정하기 위한 클릭 이벤트
        arriveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PracticeActivity를 실행하여 결과를 받음
                Intent intent = new Intent(TrainActivity.this, TrainArrivePlaceActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ARRIVE);
            }
        });

        train_between_tv_count = findViewById(R.id.train_between_tv_count);
        train_between_tv_count.setText(count + "");
        train_between_addBtn = findViewById(R.id.train_between_addBtn);
        train_between_minusBtn = findViewById(R.id.train_between_minusBtn);

        train_between_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                train_between_tv_count.setText(count + "");
            }
        });

        train_between_minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 0) {
                    count--;
                    train_between_tv_count.setText(String.valueOf(count));
                }
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
        
        

        // 버튼 초기화
        train_betweenBtn = findViewById(R.id.train_betweenBtn);
        train_onewayBtn = findViewById(R.id.train_onewayBtn);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        TextView trainBetweenTvCount = findViewById(R.id.train_between_tv_count);

        Button oneWayButton = findViewById(R.id.train_onewayBtn);
        final TextView showText = findViewById(R.id.train_between_Startcalendar);
        final TextView returnText = findViewById(R.id.train_between_Arrivecalendar);
        final ConstraintLayout layout = findViewById(R.id.train_between_4);

        oneWayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);


                constraintSet.connect(showText.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
                constraintSet.connect(showText.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);
                constraintSet.setHorizontalBias(showText.getId(), 0.55f);

                constraintSet.applyTo(layout);


                showText.setVisibility(View.VISIBLE);
                returnText.setVisibility(View.GONE);

                train_betweenBtn.setBackgroundResource(R.drawable.layout_background_train3);
                train_onewayBtn.setBackgroundResource(R.drawable.layout_background_train);

                if (selectedDateText != null) {
                    selectedDateText.setText("가는 날");
                }
                if (selectedDateText2 != null) {
                    selectedDateText2.setText("오는 날");
                }

                startTextView.setText("출발지선택");
                arriveTextView.setText("도착지선택");

                radioGroup.clearCheck(); // RadioButton 초기화
                trainBetweenTvCount.setText("0"); // 탑승객 수 초기화

            }
        });

        Button betweenButton = findViewById(R.id.train_betweenBtn);
        final TextView showText_start = findViewById(R.id.train_between_Startcalendar);
        final TextView showText_arrive = findViewById(R.id.train_between_Arrivecalendar);

        betweenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);

                // "가는 날" 버튼을 화면의 가운데로 이동
                constraintSet.connect(showText_start.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
                constraintSet.connect(showText_start.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);
                constraintSet.setHorizontalBias(showText_start.getId(), 0.31f);
                constraintSet.connect(showText_start.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, 0);
                constraintSet.setVerticalBias(showText_start.getId(), 0.31f);

                // "오는 날" 버튼을 "가는 날"과 동일한 위치로 이동
                constraintSet.connect(showText_arrive.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START, 0);
                constraintSet.connect(showText_arrive.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END, 0);
                constraintSet.setHorizontalBias(showText_arrive.getId(), 0.74f);
                constraintSet.connect(showText_arrive.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, 0);
                constraintSet.setVerticalBias(showText_arrive.getId(), 0.74f);


                constraintSet.applyTo(layout);

                showText_start.setVisibility(View.VISIBLE);
                showText_arrive.setVisibility(View.VISIBLE);

                train_betweenBtn.setBackgroundResource(R.drawable.layout_background_train);
                train_onewayBtn.setBackgroundResource(R.drawable.layout_background_train3);

                if (selectedDateText != null) {
                    selectedDateText.setText("가는 날");
                }

                startTextView.setText("출발지선택");
                arriveTextView.setText("도착지선택");

                radioGroup.clearCheck(); // RadioButton 초기화
                trainBetweenTvCount.setText("0"); // 탑승객 수 초기화

            }
        });


        Locale locale = new Locale("ko");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        selectedDateText = findViewById(R.id.train_between_Startcalendar);
        selectDateButton = findViewById(R.id.train_between_Startcalendar);

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
                        TrainActivity.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // 선택된 날짜를 TextView에 표시
                            String date = (selectedMonth + 1) + "월" + " / " + selectedDay + "일";
                            selectedDateText.setText(date);
                        },
                        year, month, day
                );
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

                datePickerDialog.show();
            }
        });


        selectedDateText2 = findViewById(R.id.train_between_Arrivecalendar);
        selectDateButton2 = findViewById(R.id.train_between_Arrivecalendar);

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
                        TrainActivity.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // 선택된 날짜를 TextView에 표시
                            String date = (selectedMonth + 1) + "월" + " / " + selectedDay + "일";
                            selectedDateText2.setText(date);
                        },
                        year, month, day
                );
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

                datePickerDialog.show();
            }
        });
        }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK && data != null) {
                String stationName2 = data.getStringExtra("stationName2");

                if (requestCode == REQUEST_CODE_START) {
                    // 출발지 데이터 설정
                    startTextView.setText(stationName2);
                } else if (requestCode == REQUEST_CODE_ARRIVE) {
                    // 도착지 데이터 설정
                    arriveTextView.setText(stationName2);
                }
            }
    }
    }

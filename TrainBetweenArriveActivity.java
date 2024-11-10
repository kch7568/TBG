package com.example.tbg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TrainBetweenArriveActivity extends AppCompatActivity {

    TextView train_between_arrive_backspace;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.train_between_arrive);

        train_between_arrive_backspace = findViewById(R.id.train_between_arrive_backspace);
        train_between_arrive_backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainBetweenActivity.class);
                startActivity(intent);
            }
        });

    }
}
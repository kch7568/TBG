package com.example.tbg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TrainOnewayArriveActivity extends AppCompatActivity {

    TextView train_oneway_arrive_backspace;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.train_oneway_arrive);

        train_oneway_arrive_backspace = findViewById(R.id.train_oneway_arrive_backspace);
        train_oneway_arrive_backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainOnewayActivity.class);
                startActivity(intent);
            }
        });



    }
}
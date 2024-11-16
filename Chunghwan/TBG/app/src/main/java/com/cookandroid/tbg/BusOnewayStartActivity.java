package com.cookandroid.tbg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BusOnewayStartActivity extends AppCompatActivity {

    TextView bus_backspace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bus_oneway_start);

        bus_backspace = findViewById(R.id.bus_backspace);
        bus_backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BusOnewayActivity.class);
                startActivity(intent);
            }
        });

    }
}
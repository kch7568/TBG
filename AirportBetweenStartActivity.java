package com.example.tbg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AirportBetweenStartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AirportAdapter airportAdapter;

    TextView airport_backspace;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.airport_between_start);

        List<Airport> airportList = new ArrayList<>();
        airportList.add(new Airport("인천공항", R.drawable.i_airport));
        airportList.add(new Airport("김포공항", R.drawable.g_airport));
        airportList.add(new Airport("김해공항", R.drawable.gim_airport));
        airportList.add(new Airport("대구공항", R.drawable.d_airport));
        airportList.add(new Airport("괌 국제공항", R.drawable.gam_airport));
        airportList.add(new Airport("나리타 국제공항", R.drawable.na_airport));
        airportList.add(new Airport("삿포로 국제공항", R.drawable.sa_airport));
        airportList.add(new Airport("간사이 국제공항", R.drawable.gan_airport));
        airportList.add(new Airport("오키나와 국제공항", R.drawable.o_airport));
        airportList.add(new Airport("후쿠오카 국제공항", R.drawable.hu_airport));
        airportList.add(new Airport("파리 국제공항", R.drawable.pa_airport));
        airportList.add(new Airport("베트남 국제공항", R.drawable.bae_airport));
        airportList.add(new Airport("홍콩 국제공항", R.drawable.hung_airport));


        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        airportAdapter = new AirportAdapter(airportList);
        recyclerView.setAdapter(airportAdapter);

        airportAdapter.setOnItemClickListener(new AirportAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String airportName) {
                // 선택된 역 이름을 Intent로 반환
                Intent resultIntent = new Intent();
                resultIntent.putExtra("airportName", airportName); // 사용자가 클릭한 역 이름을 반환
                setResult(RESULT_OK, resultIntent); // 결과 전달
                finish(); // Activity 종료
            }
        });

        // SearchView 설정
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                airportAdapter.filter(query);  // 필터링
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                airportAdapter.filter(newText);  // 실시간 필터링
                return false;
            }
        });

        airport_backspace = findViewById(R.id.airport_backspace);
        airport_backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AirportBetweenActivity.class);
                startActivity(intent);
            }
        });

    }
}
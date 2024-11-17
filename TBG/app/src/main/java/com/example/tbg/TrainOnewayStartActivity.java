package com.example.tbg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainOnewayStartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubwayAdapter subwayAdapter;

    TextView train_backspace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_oneway_start);

        List<SubwayStation> subwayStations = new ArrayList<>();
        subwayStations.add(new SubwayStation("서울역", R.drawable.s_station)); // ic_seoul.png 이미지를 drawable 폴더에 추가
        subwayStations.add(new SubwayStation("부산역", R.drawable.b_station)); // ic_busan.png 이미지 추가
        subwayStations.add(new SubwayStation("대구역", R.drawable.d_station)); // ic_daegu.png 이미지 추가
        subwayStations.add(new SubwayStation("인천역", R.drawable.i_station)); // ic_incheon.png 이미지 추가

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        subwayAdapter = new SubwayAdapter(subwayStations);
        recyclerView.setAdapter(subwayAdapter);

        // SearchView 설정
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                subwayAdapter.filter(query);  // 필터링
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                subwayAdapter.filter(newText);  // 실시간 필터링
                return false;
            }
        });


        train_backspace = findViewById(R.id.train_backspace);
        train_backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainOnewayActivity.class);
                startActivity(intent);
            }
        });

    }

}
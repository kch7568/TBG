package com.example.tbg;

import android.annotation.SuppressLint;
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
import java.util.List;

public class TrainBetweenArriveActivity extends AppCompatActivity {

    TextView train_backspace;

    private RecyclerView recyclerView;
    private SubwayAdapter subwayAdapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.train_between_arrive);

        List<SubwayStation> subwayStations = new ArrayList<>();
        subwayStations.add(new SubwayStation("서울역", R.drawable.s_station)); // ic_seoul.png 이미지를 drawable 폴더에 추가
        subwayStations.add(new SubwayStation("부산역", R.drawable.b_station));
        subwayStations.add(new SubwayStation("여수역", R.drawable.yu_station));
        subwayStations.add(new SubwayStation("용산역", R.drawable.y_station));
        subwayStations.add(new SubwayStation("대전역", R.drawable.d_station));
        subwayStations.add(new SubwayStation("광주송정역", R.drawable.g_station));
        subwayStations.add(new SubwayStation("판도역", R.drawable.p_station));
        subwayStations.add(new SubwayStation("행신역", R.drawable.h_station));
        subwayStations.add(new SubwayStation("목포역", R.drawable.m_station));
        subwayStations.add(new SubwayStation("강릉역", R.drawable.gn_station));
        subwayStations.add(new SubwayStation("충주역", R.drawable.cu_station));
        subwayStations.add(new SubwayStation("익산역", R.drawable.ic_station));
        subwayStations.add(new SubwayStation("포항역", R.drawable.po_station));


        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        subwayAdapter = new SubwayAdapter(subwayStations);
        recyclerView.setAdapter(subwayAdapter);

        subwayAdapter.setOnItemClickListener(new SubwayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String stationName2) {
                // 선택된 역 이름을 Intent로 반환
                Intent resultIntent = new Intent();
                resultIntent.putExtra("stationName2", stationName2); // 사용자가 클릭한 역 이름을 반환

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
                Intent intent = new Intent(getApplicationContext(), TrainBetweenActivity.class);
                startActivity(intent);
            }
        });

    }
}
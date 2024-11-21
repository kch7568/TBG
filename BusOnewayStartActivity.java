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

public class BusOnewayStartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BusAdapter busAdapter;

    TextView bus_backspace;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bus_oneway_start);

        List<BusExpress> busExpress = new ArrayList<>();
        busExpress.add(new BusExpress("서울역", R.drawable.s_station)); // ic_seoul.png 이미지를 drawable 폴더에 추가
        busExpress.add(new BusExpress("부산역", R.drawable.b_station));
        busExpress.add(new BusExpress("여수역", R.drawable.yu_station));
        busExpress.add(new BusExpress("용산역", R.drawable.y_station));
        busExpress.add(new BusExpress("대전역", R.drawable.d_station));
        busExpress.add(new BusExpress("광주송정역", R.drawable.g_station));
        busExpress.add(new BusExpress("판도역", R.drawable.p_station));
        busExpress.add(new BusExpress("행신역", R.drawable.h_station));
        busExpress.add(new BusExpress("목포역", R.drawable.m_station));
        busExpress.add(new BusExpress("강릉역", R.drawable.gn_station));
        busExpress.add(new BusExpress("충주역", R.drawable.cu_station));
        busExpress.add(new BusExpress("익산역", R.drawable.ic_station));
        busExpress.add(new BusExpress("포항역", R.drawable.po_station));


        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        busAdapter = new BusAdapter(busExpress);
        recyclerView.setAdapter(busAdapter);

        busAdapter.setOnItemClickListener(new BusAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String busName) {
                // 선택된 역 이름을 Intent로 반환
                Intent resultIntent = new Intent();
                resultIntent.putExtra("busName", busName); // 사용자가 클릭한 역 이름을 반환
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
                busAdapter.filter(query);  // 필터링
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                busAdapter.filter(newText);  // 실시간 필터링
                return false;
            }
        });

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
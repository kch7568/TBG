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
import java.util.List;

public class BusArrivePlaceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BusAdapter busAdapter;

    TextView bus_backspace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bus_arrive_place);

        List<BusExpress> busExpress = new ArrayList<>();
        busExpress.add(new BusExpress("서울 터미널", R.drawable.s_terminal));
        busExpress.add(new BusExpress("부산 터미널", R.drawable.b_terminal));
        busExpress.add(new BusExpress("광주 터미널", R.drawable.g_terminal));
        busExpress.add(new BusExpress("인천 터미널", R.drawable.i_terminal));
        busExpress.add(new BusExpress("서울동부 터미널", R.drawable.sd_terminal));
        busExpress.add(new BusExpress("부천 터미널", R.drawable.bu_terminal));
        busExpress.add(new BusExpress("공주 터미널", R.drawable.go_terminal));
        busExpress.add(new BusExpress("천안 터미널", R.drawable.c_terminal));
        busExpress.add(new BusExpress("아산 터미널", R.drawable.a_terminal));
        busExpress.add(new BusExpress("구미 터미널", R.drawable.gu_terminal));
        busExpress.add(new BusExpress("마산 터미널", R.drawable.m_terminal));
        busExpress.add(new BusExpress("원주 터미널", R.drawable.w_terminal));
        busExpress.add(new BusExpress("포천 터미널", R.drawable.p_terminal));


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
                Intent intent = new Intent(getApplicationContext(), BusActivity.class);
                startActivity(intent);
            }
        });

    }
}
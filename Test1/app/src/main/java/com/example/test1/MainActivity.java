package com.example.test1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText etDeparture, etDestination, etDepartureDate, etReturnDate;
    private Button btnSearch;
    private RecyclerView rvResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View 초기화
        etDeparture = findViewById(R.id.et_departure);
        etDestination = findViewById(R.id.et_destination);
        etDepartureDate = findViewById(R.id.et_departure_date);
        etReturnDate = findViewById(R.id.et_return_date);
        btnSearch = findViewById(R.id.btn_search);
        rvResults = findViewById(R.id.rv_results);

        rvResults.setLayoutManager(new LinearLayoutManager(this));

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String departure = etDeparture.getText().toString();
                String destination = etDestination.getText().toString();
                String departureDate = etDepartureDate.getText().toString();
                String returnDate = etReturnDate.getText().toString();

                // 간단한 입력값 검증
                if (departure.isEmpty() || destination.isEmpty() || departureDate.isEmpty() || returnDate.isEmpty()) {
                    Toast.makeText(MainActivity.this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                fetchAirportInfo(departure, destination, departureDate, returnDate);
            }
        });
    }

    private void fetchAirportInfo(String departure, String destination, String departureDate, String returnDate) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.odcloud.kr/api/")  // 공항 API Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AirportService airportService = retrofit.create(AirportService.class);

        // 공항 검색을 위한 파라미터 설정
        String serviceKey = "D%2BJn2FGQ1J%2FU75AoJApxRLkMKBc0UML824af9%2F1FOS3Mh7Dv7gvKZSr32g%2Ftn1LFlqgm12bCfqUaS3D7yQv2Ig%3D%3D";  // 실제 API 인증 키로 변경 필요
        Call<String> call = airportService.getAirportInfo(
                serviceKey, "csv", 10, 1);  // CSV 형식 요청

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String csvData = response.body();  // CSV 형식의 데이터를 받음

                    // CSV 데이터를 파싱하여 Airport 객체 리스트로 변환
                    List<AirportResponse.Airport> airports = parseCSV(csvData);

                    // RecyclerView에 데이터를 전달
                    rvResults.setAdapter(new AirportAdapter(airports));  // AirportAdapter에서 데이터 처리
                } else {
                    // 데이터 로딩 실패 시 메시지 표시
                    Toast.makeText(MainActivity.this, "데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // API 호출 실패 시 메시지 표시
                Toast.makeText(MainActivity.this, "API 호출 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // CSV 데이터 파싱 메소드
    private List<AirportResponse.Airport> parseCSV(String csvData) {
        List<AirportResponse.Airport> airportList = new ArrayList<>();

        // CSV 데이터를 줄 단위로 분리
        String[] lines = csvData.split("\n");

        // 첫 번째 줄은 헤더이므로 제외하고 두 번째 줄부터 시작
        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].split(",");

            // 배열 길이가 8보다 클 때만 처리하도록 조건 추가
            if (fields.length >= 8) {
                AirportResponse.Airport airport = new AirportResponse().new Airport();

                // 각 필드에 대해 null 체크를 추가하여 안전한 데이터 설정
                if (fields[0] != null && !fields[0].isEmpty()) {
                    airport.setAirportCodeIATA(fields[0]);  // IATA 코드
                }

                if (fields[6] != null && !fields[6].isEmpty()) {
                    airport.setKoreanAirportName(fields[6]);  // 한국 공항명
                }

                if (fields[7] != null && !fields[7].isEmpty()) {
                    airport.setKoreanCountryName(fields[7]);  // 한국 국가명
                }

                airportList.add(airport);
            }
        }
        return airportList;
    }
}

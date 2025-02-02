package com.cookandroid.tbg;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FilghtMain_t extends AppCompatActivity {

    private EditText etOrigin, etDestination, etDepartureDate, etReturnDate, etAdults, etClass;
    private RadioGroup rgTripType;
    private RecyclerView recyclerView;
    private FlightAdapter_t flightAdapter;
    private List<Flight_t> flightList;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apitest);

        // View 초기화
        etOrigin = findViewById(R.id.et_origin);
        etDestination = findViewById(R.id.et_destination);
        etDepartureDate = findViewById(R.id.et_departure_date);
        etReturnDate = findViewById(R.id.et_return_date);
        etAdults = findViewById(R.id.et_adults);
        etClass = findViewById(R.id.et_class);
        rgTripType = findViewById(R.id.rg_trip_type);
        recyclerView = findViewById(R.id.recycler_view);

        // RecyclerView 초기화
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        flightList = new ArrayList<>();
        flightAdapter = new FlightAdapter_t(this, flightList); // Context 추가
        recyclerView.setAdapter(flightAdapter);


        // Executor 초기화
        executorService = Executors.newSingleThreadExecutor();

        // 검색 버튼 클릭 리스너
        Button btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(v -> {
            String origin = etOrigin.getText().toString();
            String destination = etDestination.getText().toString();
            String departureDate = etDepartureDate.getText().toString();
            String returnDate = etReturnDate.getText().toString();
            String adults = etAdults.getText().toString();
            String seatClass = etClass.getText().toString();

            int selectedTripType = rgTripType.getCheckedRadioButtonId();
            if (selectedTripType == -1) {
                Toast.makeText(this, "편도 또는 왕복을 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isRoundTrip = (selectedTripType == R.id.rb_round_trip);

            if (origin.isEmpty() || destination.isEmpty() || departureDate.isEmpty() || adults.isEmpty()) {
                Toast.makeText(this, "필수 입력값을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            fetchFlightData(origin, destination, departureDate, isRoundTrip ? returnDate : "", adults, seatClass);
        });
    }

    private void fetchFlightData(String origin, String destination, String departureDate, String returnDate, String adults, String seatClass) {
        executorService.execute(() -> {
            try {
                // Amadeus Access Token 발급
                String token = getAccessToken();
                if (token == null) {
                    runOnUiThread(() -> Toast.makeText(this, "토큰 발급 실패", Toast.LENGTH_SHORT).show());
                    return;
                }

                // API 요청 URL 생성
                String urlStr = "https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=" + origin
                        + "&destinationLocationCode=" + destination
                        + "&departureDate=" + departureDate
                        + (returnDate.isEmpty() ? "" : "&returnDate=" + returnDate)
                        + "&adults=" + adults
                        + (seatClass.isEmpty() ? "" : "&travelClass=" + seatClass)
                        + "&currencyCode=KRW";

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    List<Flight_t> flights = parseFlightData(response.toString());
                    runOnUiThread(() -> {
                        flightList.clear();
                        flightList.addAll(flights);
                        flightAdapter.notifyDataSetChanged();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error: " + responseCode, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "오류 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private String getAccessToken() {
        try {
            String tokenUrl = "https://test.api.amadeus.com/v1/security/oauth2/token";
            URL url = new URL(tokenUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            String clientId = "yEw7esPQNRUs7GTJWWdr7JEQjkdB4PkD";
            String clientSecret = "iY79DGtdumkcFDEv";
            String body = "grant_type=client_credentials"
                    + "&client_id=" + clientId
                    + "&client_secret=" + clientSecret;

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
                os.flush();
            }

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.getString("access_token");
            } else {
                Log.e("AirportAPITest", "토큰 요청 실패. HTTP 코드: " + conn.getResponseCode());
                return null;
            }
        } catch (Exception e) {
            Log.e("AirportAPITest", "토큰 발급 오류: " + e.getMessage());
            return null;
        }
    }

    private List<Flight_t> parseFlightData(String response) {
        List<Flight_t> flights = new ArrayList<>();
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray data = jsonResponse.getJSONArray("data");

            for (int i = 0; i < data.length(); i++) {
                JSONObject flight = data.getJSONObject(i);
                JSONObject price = flight.getJSONObject("price");
                JSONArray itineraries = flight.getJSONArray("itineraries");

                // 첫 번째 여정 (출발)
                JSONObject firstItinerary = itineraries.getJSONObject(0);
                JSONArray segments = firstItinerary.getJSONArray("segments");
                JSONObject firstSegment = segments.getJSONObject(0);

                String departureInfo = firstSegment.getJSONObject("departure").getString("iataCode") + " -> " +
                        firstSegment.getJSONObject("departure").getString("at");
                String arrivalInfo = firstSegment.getJSONObject("arrival").getString("iataCode") + " -> " +
                        firstSegment.getJSONObject("arrival").getString("at");

                // 두 번째 여정 (복귀, 선택적)
                String returnInfo = "";
                if (itineraries.length() > 1) {
                    JSONObject secondItinerary = itineraries.getJSONObject(1);
                    JSONArray returnSegments = secondItinerary.getJSONArray("segments");
                    JSONObject returnSegment = returnSegments.getJSONObject(0);

                    returnInfo = returnSegment.getJSONObject("departure").getString("iataCode") + " -> " +
                            returnSegment.getJSONObject("departure").getString("at");
                }

                String totalPrice = price.getString("total") + " " + price.getString("currency");

                flights.add(new Flight_t(totalPrice, departureInfo, arrivalInfo, returnInfo));
            }
        } catch (Exception e) {
            Log.e("AirportAPITest", "데이터 파싱 오류: " + e.getMessage());
        }
        return flights;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}

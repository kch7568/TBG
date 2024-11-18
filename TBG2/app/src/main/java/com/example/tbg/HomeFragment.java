package com.example.tbg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import com.squareup.picasso.Picasso;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.tbg.RetrofitClient;




public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView WelcomeMessage;
    private AdView mAdView;

    private TextView locationTextView, temperatureTextView, humidityTextView, dateTextView, popTextView;

    private ImageView weatherIconImageView;

    private FusedLocationProviderClient fusedLocationClient;

    // ActivityResultLauncher 선언 (권한 요청을 처리하는 런처)
    private ActivityResultLauncher<String[]> requestPermissionLauncher;


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // MobileAds 초기화
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // AdView 찾아서 광고 로드
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setBackgroundColor(Color.TRANSPARENT); // 광고 배경 투명하게 설정
        mAdView.loadAd(adRequest);

        // SharedPreferences에서 광고 상태 확인
        SharedPreferences prefs = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        boolean adEnabled = prefs.getBoolean("ad_enabled", true);

        // 광고 상태에 따라 표시/숨김 처리
        if (adEnabled) {
            mAdView.setVisibility(View.VISIBLE);
            mAdView.loadAd(adRequest);
        } else {
            mAdView.setVisibility(View.GONE);
        }

        // UI 요소 초기화
        WelcomeMessage = view.findViewById(R.id.WelcomeMessage);
        locationTextView = view.findViewById(R.id.locationTextView);
        temperatureTextView = view.findViewById(R.id.temperatureTextView);
        humidityTextView = view.findViewById(R.id.humidityTextView);
        dateTextView = view.findViewById(R.id.dateTextView);
        weatherIconImageView = view.findViewById(R.id.weatherIconImageView);
        popTextView = view.findViewById(R.id.popTextView);


        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // ActivityResultLauncher 초기화 (권한 요청 처리)
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    if (fineLocationGranted != null && fineLocationGranted) {
                        // ACCESS_FINE_LOCATION 권한이 허용됨
                        getLastKnownLocation();
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // ACCESS_COARSE_LOCATION 권한만 허용됨 (대략적인 위치 사용)
                        getLastKnownLocation();
                    } else {
                        // 권한이 거부됨
                        locationTextView.setText("위치 권한이 필요합니다.");
                    }
                });

        // 권한 확인 및 요청
        checkAndRequestPermissions();






        // 이미지뷰 클릭 리스너 추가
        ImageView imageView1 = view.findViewById(R.id.imageView1);
        ImageView imageView2 = view.findViewById(R.id.imageView2);
        ImageView imageView3 = view.findViewById(R.id.imageView3);
        ImageView imageView4 = view.findViewById(R.id.imageView4);

        imageView1.setOnClickListener(v -> {
            showTravelInfoDialog("여행지 1", "여행지 1에 대한 설명입니다.", R.drawable.travledescription1);
        });

        imageView2.setOnClickListener(v -> {
            showTravelInfoDialog("여행지 2", "여행지 2에 대한 설명입니다.", R.drawable.travledescription2);
        });

        imageView3.setOnClickListener(v -> {
            showTravelInfoDialog("여행지 3", "여행지 3에 대한 설명입니다.", R.drawable.travledescription3);
        });

        imageView4.setOnClickListener(v -> {
            showTravelInfoDialog("여행지 4", "여행지 4에 대한 설명입니다.", R.drawable.travledescription4);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Fragment가 다시 보일 때마다 광고 상태 확인
        SharedPreferences prefs = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        boolean adEnabled = prefs.getBoolean("ad_enabled", true);
        if (mAdView != null) {
            if (adEnabled) {
                mAdView.setVisibility(View.VISIBLE);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            } else {
                mAdView.setVisibility(View.GONE);
            }
        }
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 이미 권한이 허용된 경우 위치 가져오기
            getLastKnownLocation();
        } else {
            // 권한이 없으면 사용자에게 요청
            requestPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                // 위치 정보를 간단히 표시
                                locationTextView.setText("현재 위치 정보를 가져오는 중...");

                                // Geocoding API를 사용하여 주소 가져오기
                                getAddressFromLocation(latitude, longitude);

                                // 위도와 경도를 사용하여 날씨 API 호출 가능
                                getWeatherData(latitude, longitude);  // 날씨 데이터 가져오기 메서드 호출
                            } else {
                                locationTextView.setText("위치를 찾을 수 없습니다.");
                            }
                        }
                    });
        }
    }
    private void getWeatherData(double latitude, double longitude) {

        String apiKey = "myapi";  // OpenWeatherMap에서 발급받은 API 키
        String units = "metric";  // 섭씨 온도 단위

        WeatherService weatherService = RetrofitClient.getWeatherClient().create(WeatherService.class);
        Call<WeatherResponse> call = weatherService.getWeatherData(latitude, longitude, apiKey, units);

        call.enqueue(new Callback<WeatherResponse>() {

            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();

                    // 온도,습도,강수확률 업데이트
                    temperatureTextView.setText(String.format(Locale.getDefault(), "%.1f°C", weatherResponse.main.temp));
                    humidityTextView.setText(String.format(Locale.getDefault(), "습도: %d%%", weatherResponse.main.humidity));
                    popTextView.setText(String.format(Locale.getDefault(), "강수확률: %.0f%%", weatherResponse.main.pop * 100));

                    // 날씨 아이콘 업데이트 (Picasso 라이브러리로 이미지 로드),
                    String iconUrl = "https://openweathermap.org/img/wn/" + weatherResponse.weather[0].icon + "@2x.png";
                    Picasso.get().load(iconUrl).into(weatherIconImageView);

                    // 날짜 업데이트
                    long timestamp = weatherResponse.timestamp * 1000L;
                    SimpleDateFormat sdf = new SimpleDateFormat("M월 d일 E요일", Locale.KOREA);//요일 한국어로 나오게 지정
                    dateTextView.setText(sdf.format(new Date(timestamp))); //텍스트뷰에 날짜 업데이트
                } else {
                    // API 응답 로그 추가
                    Log.e("WeatherAPI", "Error: " + response.code() + " " + response.message());
                    temperatureTextView.setText("날씨 정보를 가져올 수 없습니다.");
                    Log.d("WeatherAPI", "Request URL: " + call.request().url());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("WeatherAPI", "Network error: " + t.getMessage());
                temperatureTextView.setText("네트워크 오류가 발생했습니다.");
                t.printStackTrace();
            }
        });
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        String latlng = latitude + "," + longitude;
        String apiKey = "myapi"; // google map api키,일 평균 900회 정도의 요청을 무료로 사용 가능
        String language = "ko"; // 한국어로 결과 요청

        GeocodingService service = RetrofitClient.getGeocodingClient().create(GeocodingService.class);
        Call<GeocodingResponse> call = service.getAddressFromLatLng(latlng, apiKey, language);

        call.enqueue(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GeocodingResponse geocodingResponse = response.body();
                    if (!geocodingResponse.results.isEmpty()) {
                        String fullAddress = geocodingResponse.results.get(0).formattedAddress;
                        String simplifiedAddress = simplifyAddress(fullAddress);
                        locationTextView.setText(simplifiedAddress);
                    }
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                locationTextView.setText("주소를 가져오는데 실패했습니다.");
            }
        });
    }
    private String simplifyAddress(String fullAddress) {
        String[] addressComponents = fullAddress.split(" ");
        if (addressComponents.length > 1) {
            // 첫 번째와 두 번째 구성 요소만 사용 예시) 서울특별시 동작구
            return addressComponents[0] + " " + addressComponents[1];
        } else {
            return fullAddress; // 주소가 너무 짧으면 전체 주소 반환해줌
        }
    }

    private void showTravelInfoDialog(String title, String message, int imageResId) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_travel_info, null);

        ImageView dialogImageView = dialogView.findViewById(R.id.dialogImageView);
        dialogImageView.setImageResource(imageResId);

        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        dialogTitle.setText(title);

        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        dialogMessage.setText(message);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        TextView closeButton = dialogView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}


package com.cookandroid.tbg;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.tbg.RetrofitClient;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;




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

        // 게시글 데이터를 가져오는 메서드 호출
        fetchTopPosts(view);


        return view;
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

        String apiKey = "";  // OpenWeatherMap에서 발급받은 API 키
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
        String apiKey = ""; // google map api키,일 평균 900회 정도의 요청을 무료로 사용 가능
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
            return addressComponents[1] + " " + addressComponents[2] + " " + addressComponents[3];
        } else {
            return fullAddress; // 주소가 너무 짧으면 전체 주소 반환해줌
        }
    }
    public void setWelcomeMessageText(String message) {
        WelcomeMessage.setText(message);
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



    private void fetchTopPosts(View view) {
        PostService postService = RetrofitClient.getPostClient().create(PostService.class);
        Call<List<PostItem>> call = postService.getTopPosts();

        call.enqueue(new Callback<List<PostItem>>() {
            @Override
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PostItem> topPosts = response.body();

                    Log.d("TopPosts", "받아온 게시글 수: " + topPosts.size());

                    if (topPosts.size() >= 2) {
                        // 각 필드에 대해 null 기본값 처리
                        for (PostItem post : topPosts) {
                            post.setPostImageUrl(post.getPostImageUrl() != null ? post.getPostImageUrl() : "");
                            post.setProfileImageUrl(post.getProfileImageUrl() != null ? post.getProfileImageUrl() : "");
                            post.setVideoUrl(post.getVideoUrl() != null ? post.getVideoUrl() : "");
                            post.setAuthor(post.getAuthor() != null ? post.getAuthor() : "Unknown");
                        }

                        // 첫 번째와 두 번째 게시글 데이터 처리
                        processPostData(view, topPosts.get(0),
                                R.id.postImage1, R.id.postProfile1, R.id.postTitle1,
                                R.id.postNickname1, R.id.postLikes1, R.id.noticeboard1);

                        processPostData(view, topPosts.get(1),
                                R.id.postImage2, R.id.postProfile2, R.id.postTitle2,
                                R.id.postNickname2, R.id.postLikes2, R.id.noticeboard2);
                    }
                } else {
                    Log.e("TopPostsAPI", "Failed to fetch posts. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                Log.e("TopPostsAPI", "Error: " + t.getMessage());
            }
        });
    }



    /**
     * 게시글 데이터를 처리하는 메서드
     */
    private void processPostData(View view, PostItem post,
                                 int imageViewId, int profileViewId,
                                 int titleViewId, int nicknameViewId,
                                 int likesViewId, int noticeBoardId) {
        ImageView postImageView = view.findViewById(imageViewId);
        ImageView postProfileView = view.findViewById(profileViewId);
        TextView postTitleView = view.findViewById(titleViewId);
        TextView postNicknameView = view.findViewById(nicknameViewId);
        TextView postLikesView = view.findViewById(likesViewId);

        // 동영상 또는 이미지 썸네일 설정
        String postVideoUrl = post.getVideoUrl() != null ? post.getVideoUrl().replace("localhost", "10.0.2.2") : null;
        String postImageUrl = post.getPostImageUrl() != null ? post.getPostImageUrl().replace("localhost", "10.0.2.2") : null;

        if (postVideoUrl != null && !postVideoUrl.isEmpty()) {
            Bitmap thumbnail = getVideoThumbnail(postVideoUrl);
            if (thumbnail != null) {
                postImageView.setImageBitmap(thumbnail);
            } else {
                postImageView.setImageResource(R.drawable.tbg_icon);
            }
        } else if (postImageUrl != null && !postImageUrl.isEmpty()) {
            Picasso.get().load(postImageUrl)
                    .placeholder(R.drawable.travle_info_border)
                    .into(postImageView);
        } else {
            postImageView.setImageResource(R.drawable.tbg_icon);
        }

        // 프로필 이미지 설정
        String profileImageUrl = post.getProfileImageUrl();
        if (profileImageUrl != null) {
            profileImageUrl = profileImageUrl.replace("localhost", "10.0.2.2");
            Picasso.get().load(profileImageUrl)
                    .placeholder(R.drawable.default_profile_image)
                    .into(postProfileView);
        }

        // 텍스트 설정
        postTitleView.setText(post.getTitle());
        postNicknameView.setText(post.getNickname());
        postLikesView.setText("좋아요: " + post.getLikes());




        Log.d("테테스트번호", String.valueOf(post.getPostNum())  != null ? String.valueOf(post.getPostNum()) : "null");
        Log.d("테테스트제목", post.getTitle() != null ? post.getTitle() : "Title is null");
        Log.d("테테스트닉넴", post.getNickname() != null ? post.getNickname() : "Nickname is null");
        Log.d("테테스트날짜", post.getDate() != null ? post.getDate() : "Date is null");
        Log.d("테테스트내용", post.getContent() != null ? post.getContent() : "Content is null");

        if (post.getPostImageUrl() != null) {
            Log.d("테테스트사진", post.getPostImageUrl());
        } else {
            Log.d("테테스트사진", "PostImageUrl is null");
        }

        if (post.getVideoUrl() != null) {
            Log.d("테테스트동영상", post.getVideoUrl());
        } else {
            Log.d("테테스트동영상", "VideoUrl is null");
        }

        if (post.getProfileImageUrl() != null) {
            Log.d("테테스트프로필사진", post.getProfileImageUrl());
        } else {
            Log.d("테테스트프로필사진", "ProfileImageUrl is null");
        }

        Log.d("테테스트조회수", post.getViews() > 0 ? "Views: " + post.getViews() : "Views is 0 or null");
        Log.d("테테스트좋아요", post.getLikes() > 0 ? "Likes: " + post.getLikes() : "Likes is 0 or null");

        if (post.getAuthor() != null) {
            Log.d("테테스트작성자ID", post.getAuthor());
        } else {
            Log.d("테테스트작성자ID", "AuthorId is null");
        }



        // **게시글 클릭 시 상세 페이지로 이동**
        view.findViewById(noticeBoardId).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PostDetailActivity.class);

            // 게시글 번호를 String으로 변환하여 전달
            intent.putExtra("postNum", String.valueOf(post.getPostNum()));

            // 제목, 작성자, 날짜, 내용 등 기본 정보 전달
            intent.putExtra("title", post.getTitle());
            intent.putExtra("author", post.getNickname()); // 작성자 이름 전달
            intent.putExtra("date", post.getDate());
            intent.putExtra("content", post.getContent()); // 본문 내용 전달

            // 이미지와 비디오 URL 및 프로필 이미지 URL 전달
            if(post.getPostImageUrl() != null) {
                intent.putExtra("postImageUrl", post.getPostImageUrl()); // 게시글 이미지 URL 전달
            }if(post.getProfileImageUrl() != null) {
                intent.putExtra("profileImageUrl", post.getProfileImageUrl()); // 프로필 이미지 URL 전달
            }if( post.getVideoUrl() != null) {
                intent.putExtra("postVideoUrl", post.getVideoUrl());
            }
            // 조회수와 좋아요 개수 추가 전달
            intent.putExtra("views", post.getViews()); //이게 널일듯
            intent.putExtra("likes", post.getLikes());

            // 작성자 ID 전달
            intent.putExtra("authorId", post.getAuthor());

            // 액티비티 시작
            startActivity(intent);
        });

    }


    private Bitmap getVideoThumbnail(String videoPath) {
        Log.d("HomeFragment", "Video path for thumbnail: " + videoPath);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath, new HashMap<>());
            return retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (Exception e) {
            Log.e("HomeFragment", "Error generating video thumbnail", e);
            return null;
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                Log.e("HomeFragment", "Error releasing MediaMetadataRetriever", e);
            }
        }
    }

}


package com.cookandroid.tbg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.window.SplashScreenView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 100; // 알림 권한 요청 코드
    private OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutOnboardingIndicators;
    private MaterialButton buttonOnboardingAction;
    private ViewPager2 onboardingViewPager;
    private final ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            setCurrentOnboardingIndicator(position);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Android 12 이상 SplashScreen 처리
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSplashScreen().setOnExitAnimationListener(SplashScreenView::remove);
        }

        setContentView(R.layout.activity_main);

        // 권한 요청
        requestNotificationPermission();

        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);
        buttonOnboardingAction = findViewById(R.id.buttonOnboardingAction);
        onboardingViewPager = findViewById(R.id.onboardingViewPager);

        setupOnboardingItems();
        onboardingViewPager.setAdapter(onboardingAdapter);

        setupOnboardingIndicators();
        setCurrentOnboardingIndicator(0);

        onboardingViewPager.registerOnPageChangeCallback(onPageChangeCallback);

        buttonOnboardingAction.setOnClickListener(view -> {
            if (onboardingViewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
                onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem() + 1); // 다음 페이지로 이동
            } else {
                // 마지막 페이지에 도달한 경우
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                // 애니메이션 설정 (fade-in, fade-out)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                // 애니메이션 후 종료
                new Handler(Looper.getMainLooper()).postDelayed(() -> finish(), 300);
            }
        });

    }

    /**
     * 알림 권한 요청 (Android 13 이상)
     */
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13(API 33) 이상
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    /**
     * 권한 요청 결과 처리
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 알림 권한 허용됨
            } else {
                // 알림 권한 거부됨
                // 필요 시 사용자에게 권한 거부 안내
                showPermissionDeniedMessage();
            }
        }
    }

    /**
     * 알림 권한 거부 안내 메시지
     */
    private void showPermissionDeniedMessage() {
        // 사용자에게 알림 권한 필요성을 안내하거나 설정으로 이동 옵션 제공
        // 예: Toast, Dialog 등으로 사용자 안내
        Toast.makeText(this, "알림 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> onboardingItems = new ArrayList<>();

        OnboardingItem itemPayOnline = new OnboardingItem();
        itemPayOnline.setTitle("여행지 추천!");
        itemPayOnline.setDescription("가고 싶은 곳이 \n있으신가요?");
        itemPayOnline.setImage(R.drawable.onboarding1);

        OnboardingItem itemOnTheWay = new OnboardingItem();
        itemOnTheWay.setTitle("교통수단 예약 가능!");
        itemOnTheWay.setDescription("각종 교통수단과 \n함께 할 수 있어요");
        itemOnTheWay.setImage(R.drawable.onboarding2);

        OnboardingItem itemEatTogether = new OnboardingItem();
        itemEatTogether.setTitle("손쉽게 시작하는 여행!");
        itemEatTogether.setDescription("타볼까와\n여행을 시작해봐요!");
        itemEatTogether.setImage(R.drawable.onboarding3);

        onboardingItems.add(itemPayOnline);
        onboardingItems.add(itemOnTheWay);
        onboardingItems.add(itemEatTogether);

        onboardingAdapter = new OnboardingAdapter(onboardingItems);
    }

    private void setupOnboardingIndicators() {
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(20, 0, 20, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplication());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setCurrentOnboardingIndicator(int index) {
        int childCount = layoutOnboardingIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive)
                );
            }
        }
        if (index == onboardingAdapter.getItemCount() - 1) {
            buttonOnboardingAction.setText("시작하기");
        } else {
            buttonOnboardingAction.setText("다음");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (getWindow() != null) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // ViewPager2 콜백 해제
        if (onboardingViewPager != null) {
            onboardingViewPager.unregisterOnPageChangeCallback(onPageChangeCallback);
        }
    }
}

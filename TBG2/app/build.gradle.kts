plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.tbg"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tbg"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures{
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation (libs.appcompat)
    implementation (libs.material)
    implementation (libs.activity)
    implementation (libs.constraintlayout)
    testImplementation (libs.junit)
    androidTestImplementation (libs.ext.junit)
    androidTestImplementation (libs.espresso.core)
    implementation ("androidx.viewpager2:viewpager2:1.1.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.google.android.gms:play-services-ads:23.5.0") //AD몹 (광고 배너 표시)
    implementation ("com.squareup.retrofit2:retrofit:2.9.0") //API 호출을 위한 Http 클라이언트
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.picasso:picasso:2.71828") //이미지 로드 라이브러리
    implementation("com.google.android.gms:play-services-location:21.3.0") //gps 기반 위치 정보


}
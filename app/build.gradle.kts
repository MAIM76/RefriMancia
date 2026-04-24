plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.refrimancia"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.refrimancia"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.glide)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // Retrofit para las peticiones a la API
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    
    // UCrop para recortar imágenes
    implementation(libs.ucrop)
    
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation(libs.cardview)
    implementation(libs.fragment)
    implementation(libs.recyclerview)
    
    // Red - Retrofit, OkHttp, Gson
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    
    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

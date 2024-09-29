plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.developerdaya.mvvmexample"
    compileSdk = 34
    
    dataBinding {
        enable = true
    }
      viewBinding {
        enable = true
    }


    defaultConfig {
        applicationId = "com.developerdaya.mvvmexample"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    dependencies {
        implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
        implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
        implementation ("com.squareup.retrofit2:retrofit:2.9.0")
        implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation ("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
        implementation ("io.reactivex.rxjava2:rxjava:2.2.20")
        implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")
        implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    }






}
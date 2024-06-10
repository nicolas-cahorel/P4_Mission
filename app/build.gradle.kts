plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
}

android {
    namespace = "com.aura"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.aura"
        minSdk = 24
        targetSdk = 33
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
    buildFeatures {
        viewBinding = true
    }


}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.annotation:annotation:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:3.0.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.fragment:fragment-ktx:1.7.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Moshi JSON Library
    implementation("com.squareup.moshi:moshi:1.15.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

    // Retrofit for Network Requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Koin Dependency Injection
    implementation("io.insert-koin:koin-android:3.5.6")
    implementation("io.insert-koin:koin-core:3.5.6")
    implementation("io.insert-koin:koin-android-compat:3.5.0")
    implementation("io.insert-koin:koin-androidx-scope:3.5.0")
    implementation("io.insert-koin:koin-androidx-viewmodel:3.5.0")
    implementation("io.insert-koin:koin-androidx-fragment:3.5.0")
    implementation("io.insert-koin:koin-androidx-workmanager:3.5.0")
    implementation("io.insert-koin:koin-androidx-navigation:3.5.0")
    implementation("io.insert-koin:koin-androidx-compose:3.5.0") // If using Jetpack Compose
}
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
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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

  packaging {
    // Option 1: Exclude all META-INF/INDEX.LIST files
    resources.excludes.add ("META-INF/INDEX.LIST")
    resources.excludes.add("META-INF/io.netty.versions.properties")

    // Option 2: Merge META-INF/INDEX.LIST files
    //merge 'META-INF/INDEX.LIST'
  }
}

dependencies {

  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.annotation:annotation:1.8.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("com.google.firebase:firebase-crashlytics-buildtools:3.0.0")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

  implementation("androidx.fragment:fragment-ktx:1.7.1")

  val ktorVersion ="2.3.10"
  implementation("io.ktor:ktor-server-core:$ktorVersion")
  implementation("io.ktor:ktor-server-netty:$ktorVersion")
  implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
  implementation("io.ktor:ktor-server-swagger:$ktorVersion")
  implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
  implementation("io.ktor:ktor-server-cors:$ktorVersion")

  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

}
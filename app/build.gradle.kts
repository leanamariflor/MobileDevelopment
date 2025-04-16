plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.anime.aniwatch"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.anime.aniwatch"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    buildFeatures {
        viewBinding = true
    }
}


dependencies {

    // 🔹 Firebase dependencies (only keep the KTX versions)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.storage.ktx)

    // 🔹 Third-party Libraries
    implementation(libs.circleimageview)
    implementation(libs.googleprogressbar)
    implementation(libs.picasso)

    // 🔹 AndroidX Core Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // 🔹 Material Design Library
    implementation(libs.material)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.glance.preview)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.preference)

    // 🔹 Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // 🔹 ViewPager2 & RecyclerView
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.core:core:1.7.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // 🔹 Kotlin Coroutines
    implementation("com.facebook.shimmer:shimmer:0.5.0")
}

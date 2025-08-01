plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
//    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.clubsconnect"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.clubsconnect"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.accompanist.permissions)

    // CameraX
//    implementation(libs.androidx.camera.core)
//    implementation(libs.androidx.camera.camera2)
//    implementation(libs.androidx.camera.lifecycle)
//    implementation(libs.androidx.camera.view)

    val cameraxVersion = "1.3.0-rc01"

    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-video:$cameraxVersion")

    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.camera:camera-extensions:$cameraxVersion")
    // ML Kit for Barcode Scanning
    implementation(libs.barcode.scanning)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.adapter.rxjava2) // Optional if using RxJava
    implementation(libs.kotlinx.coroutines.android) // Required for coroutines support


    //qr code
    implementation ("com.google.zxing:core:3.5.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    // Coil for Jetpack Compose
    implementation(libs.coil.compose)

    //google fonts
    implementation(libs.androidx.ui.text.google.fonts)
    //google icons
    implementation(libs.androidx.material.icons.extended)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // ViewModel for Jetpack Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Optional: LiveData support
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Optional: SavedState module for ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    //navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.bom)

    //firebase
    //location
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    // Import the BoM for the Firebase platform
//    implementation(libs.firebase.bom.v3280)
//    implementation (libs.firebase.firestore.ktx)

//    implementation(libs.firebase.analytics)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
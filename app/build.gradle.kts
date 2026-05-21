plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.develop.films"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.develop.films"
        minSdk = 26
        targetSdk = 36
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
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // ROOM (Исправлено и расширено)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.navigation:navigation-compose:2.7.3")

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)


    // ОСНОВНАЯ БИБЛИОТЕКА HILT
    implementation("com.google.dagger:hilt-android:2.57.2")

    // КОМПИЛЯТОР: Используем KSP вместо kapt
    // (Старый способ: kapt("com.google.dagger:hilt-android-compiler:2.57.2"))
    ksp("com.google.dagger:hilt-android-compiler:2.57.2")

    // Jetpack интеграции (ViewModel, Compose, Navigation) - актуальные версии
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")    // Для Compose
    implementation("androidx.hilt:hilt-navigation-fragment:1.3.0")   // Для Fragments с NavGraph
    implementation("androidx.hilt:hilt-work:1.3.0")                 // Для WorkManager
    ksp("androidx.hilt:hilt-compiler:1.3.0")

    // WorkManager (with PendingIntent flag fix for Android 12+)
    implementation(libs.androidx.work.runtime)

    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.realtime.db)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
}

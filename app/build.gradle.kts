plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.develop.films"
    compileSdk = 35 // Рекомендую пока оставить 35, так как 36 (Android 17) еще в глубокой альфе и может конфликтовать с библиотеками

    defaultConfig {
        applicationId = "com.develop.films"
        minSdk = 26
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
}
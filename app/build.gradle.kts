plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

dependencies {
    // Other modules
    implementation(project(mapOf("path" to ":navigation")))
    implementation(project(mapOf("path" to ":auth")))
    implementation(project(mapOf("path" to ":attendance")))

    // Basics
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.activity:activity-compose:1.3.0-rc01")

    // Compose
    implementation("androidx.compose.ui:ui:1.0.0-rc01")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-beta08")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.12.0")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:1.0.0-rc01")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:1.0.0-rc01")
    // Material Design
    implementation("androidx.compose.material:material:1.0.0-rc01")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:1.0.0-rc01")
    implementation("androidx.compose.material:material-icons-extended:1.0.0-rc01")
    // Integration with observables
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0-rc01")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.0.0-rc01")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-compose:1.3.0-rc01")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.0-rc01")
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.3.5")
    // OAuth2
    implementation("net.openid:appauth:0.9.0")
    // Hilt
    implementation("com.google.dagger:hilt-android:2.35")
    kapt("com.google.dagger:hilt-android-compiler:2.35")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0-alpha03")
}

android {
    compileSdk = 30
    defaultConfig {
        applicationId = "org.robojackets.apiary"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-beta07"
    }
}
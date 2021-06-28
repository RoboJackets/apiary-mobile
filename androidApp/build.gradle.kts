plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android")
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")

    // Compose
    implementation("androidx.compose.ui:ui:1.0.0-beta09")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-beta08")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.12.0")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:1.0.0-beta09")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:1.0.0-beta09")
    // Material Design
    implementation("androidx.compose.material:material:1.0.0-beta09")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:1.0.0-beta09")
    implementation("androidx.compose.material:material-icons-extended:1.0.0-beta09")
    // Integration with observables
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0-beta09")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.0.0-beta09")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-compose:1.3.0-beta02")
    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.0-beta09")
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha03")
    // Ktor
    implementation("io.ktor:ktor-client-android:1.6.0")
    // OAuth2
    implementation("net.openid:appauth:0.9.0")
}

android {
    compileSdk = 30
    defaultConfig {
        applicationId = "org.robojackets.apiary.android"
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
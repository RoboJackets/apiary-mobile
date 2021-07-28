plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

hilt {
    enableExperimentalClasspathAggregation = true
}

dependencies {
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.activity:activity-compose:1.3.0-rc02")
    androidTestImplementation("junit:junit:4.13.2")

    // Compose
    implementation("androidx.compose.ui:ui:1.0.0-rc02")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-beta08")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:1.0.0-rc02")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:1.0.0-rc02")
    // Material Design
    implementation("androidx.compose.material:material:1.0.0-rc02")

    // Integration with observables
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0-rc02")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.0.0-rc02")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-compose:1.3.0-rc02")
    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.0-rc02")
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha05")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.35")
    kapt("com.google.dagger:hilt-android-compiler:2.35")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
}

android {
    compileSdk = 30
    defaultConfig {
//        applicationId = "org.robojackets.apiary.navigation"
        minSdk = 21
        targetSdk = 30
//        versionCode = 1
//        versionName = "1.0"
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-rc02"
    }
    hilt {
        enableExperimentalClasspathAggregation = true
    }
}
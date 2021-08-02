plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.android.gms.oss-licenses-plugin")
}

dependencies {
    // Other modules
    implementation(project(mapOf("path" to ":navigation")))
    implementation(project(mapOf("path" to ":auth")))
    implementation(project(mapOf("path" to ":attendance")))
    implementation(project(mapOf("path" to ":base")))

    // Basics
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.activity:activity-compose:1.3.0")
    androidTestImplementation("junit:junit:4.13.2")

    // Compose
    implementation("androidx.compose.ui:ui:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-beta08")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.12.0")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:1.0.0")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:1.0.0")
    // Material Design
    implementation("androidx.compose.material:material:1.0.0")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:1.0.0")
    implementation("androidx.compose.material:material-icons-extended:1.0.0")
    // Integration with observables
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-compose:1.3.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:28.3.0"))

    // NFC
    implementation(files("../libs/nxpnfcandroidlib-release.aar"))
    implementation("com.google.firebase:firebase-core") // Required when including TapLinx (line above) manually

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.0")
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.3.5")
    // Hilt
    implementation("com.google.dagger:hilt-android:2.35")
    kapt("com.google.dagger:hilt-android-compiler:2.35")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0-alpha03")

    implementation("androidx.preference:preference-ktx:1.1.1")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    // Settings UI
    implementation("com.github.alorma:compose-settings:0.2.0")

    // Collects open-source license information
    implementation("com.google.android.gms:play-services-oss-licenses:17.0.0")

    // Chrome Custom Tabs
    implementation("androidx.browser:browser:1.3.0")
}

hilt {
    enableExperimentalClasspathAggregation = true
}

android {
    signingConfigs {
        create("release") {
        }
    }
    compileSdk = 30
    defaultConfig {
        applicationId = "org.robojackets.apiary"
        minSdk = 24 // FIXME: this is temporary to workaround a Compose bug (https://issuetracker.google.com/issues/194289155)
        targetSdk = 30
        versionCode = 2
        versionName = "1.0.1"
        vectorDrawables {
            useSupportLibrary = true
        }
        manifestPlaceholders["appAuthRedirectScheme"] = "org.robojackets.apiary"
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
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0"
    }
    hilt {
        enableExperimentalClasspathAggregation = true
    }
}
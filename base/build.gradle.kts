plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

dependencies {
    androidTestImplementation(TestDependencies.junit)

    // Compose
    api(ComposeDependencies.compose_ui)
    // Tooling support (Previews, etc.)
    implementation(ComposeDependencies.compose_ui_tooling)
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    api(ComposeDependencies.compose_foundation)
    // Material Design
    api(ComposeDependencies.compose_material)

    // UI Tests
    androidTestImplementation(ComposeDependencies.compose_ui_test)

    // Hilt
    implementation(HiltDependencies.hilt)
    kapt(HiltDependencies.hilt_android_compiler)

    api(AndroidToolDependencies.krate)

    // NFC
    implementation(platform(FirebaseDependencies.firebase_bom))
    compileOnly(files(NfcDependencies.nxp_nfc_android_aar_path))
    implementation(FirebaseDependencies.firebase_core) // Required when including TapLinx (line above) manually

    coreLibraryDesugaring(AndroidToolDependencies.android_tools_desugar_jdk)
}

hilt {
    enableExperimentalClasspathAggregation = true
}

android {
    compileSdk = 30
    defaultConfig {
        minSdk = 24
        targetSdk = 30

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
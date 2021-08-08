plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

dependencies {
    api(project(mapOf("path" to ":base")))

    implementation(MaterialDependencies.material_android)
    androidTestImplementation(TestDependencies.junit)

    implementation(ComposeDependencies.compose_ui)
    implementation(ComposeDependencies.compose_material)
    implementation(ComposeDependencies.lifecycle_viewmodel_compose)

    // UI Tests
    androidTestImplementation(ComposeDependencies.compose_ui_test)

    // Hilt
    implementation(HiltDependencies.hilt)
    kapt(HiltDependencies.hilt_android_compiler)

    // NFC
    implementation(platform(FirebaseDependencies.firebase_bom))
    compileOnly(files(NfcDependencies.nxp_nfc_android_aar_path))
    implementation(FirebaseDependencies.firebase_core) // Required when including TapLinx (line above) manually

    coreLibraryDesugaring(AndroidToolDependencies.android_tools_desugar_jdk)
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
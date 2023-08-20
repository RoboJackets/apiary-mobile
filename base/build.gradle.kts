plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

dependencies {
    // Other modules (none right now)

    // Dependencies
    coreLibraryDesugaring(AndroidToolDependencies.android_tools_desugar_jdk)
    api(AndroidToolDependencies.krate)
    implementation(AndroidToolDependencies.sentry)
    implementation(AndroidToolDependencies.timber)

    implementation(ComposeDependencies.compose_foundation)
    implementation(ComposeDependencies.compose_material)
    implementation(ComposeDependencies.compose_material3)
    implementation(ComposeDependencies.compose_ui)
    implementation(ComposeDependencies.compose_ui_tooling)
    implementation(ComposeDependencies.accompanist_nav_material)

    implementation(MaterialDependencies.material_android)

    implementation(HiltDependencies.hilt)
    kapt(HiltDependencies.hilt_android_compiler)

    implementation(NetworkDependencies.moshi)
    kapt(NetworkDependencies.moshi_kotlin_codegen)
    implementation(NetworkDependencies.retrofit)
    implementation(NetworkDependencies.sandwich)

    implementation(platform(NfcDependencies.nfc_firebase_bom))
    implementation(NfcDependencies.nfc_firebase_analytics) // Firebase BoM and Analytics (f/k/a Core) are required when including TapLinx (line below) manually
    compileOnly(files(NfcDependencies.nxp_nfc_android_aar_path))

    // Test dependencies
    androidTestImplementation(ComposeDependencies.compose_ui_test)
    androidTestImplementation(TestDependencies.junit)
}

hilt {
    enableExperimentalClasspathAggregation = true
}

android {
    compileSdk = 33
    defaultConfig {
        minSdk = 21
        targetSdk = 33

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    namespace = "org.robojackets.apiary.base"
    hilt {
        enableExperimentalClasspathAggregation = true
    }
}
plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

dependencies {
    // Other modules
    implementation(project(mapOf("path" to ":base")))
    implementation(project(mapOf("path" to ":navigation")))
    implementation("androidx.navigation:navigation-common-ktx:2.3.5")

    // Dependencies
    coreLibraryDesugaring(AndroidToolDependencies.android_tools_desugar_jdk)
    implementation(AndroidToolDependencies.timber)

    implementation(ComposeDependencies.compose_material)
    implementation(ComposeDependencies.compose_ui)
    implementation(ComposeDependencies.lifecycle_viewmodel_compose)

    implementation(HiltDependencies.hilt)
    kapt(HiltDependencies.hilt_android_compiler)

    implementation(MaterialDependencies.material_android)

    implementation(NetworkDependencies.moshi)
    kapt(NetworkDependencies.moshi_kotlin_codegen)
    implementation(NetworkDependencies.okhttp)
    implementation(platform(NetworkDependencies.okhttp_bom))
    implementation(NetworkDependencies.retrofit)
    implementation(NetworkDependencies.retrofuture)
    implementation(NetworkDependencies.sandwich)

    implementation(platform(NfcDependencies.nfc_firebase_bom))
    implementation(NfcDependencies.nfc_firebase_core) // Firebase BoM and Core are required when including TapLinx (line below) manually
    compileOnly(files(NfcDependencies.nxp_nfc_android_aar_path))

    // Test dependencies
    androidTestImplementation(ComposeDependencies.compose_ui_test)

    androidTestImplementation(TestDependencies.junit)
}

android {
    compileSdk = 31
    defaultConfig {
        minSdk = 21
        targetSdk = 31
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
        kotlinCompilerExtensionVersion = "1.0.5"
    }
    hilt {
        enableExperimentalClasspathAggregation = true
    }
}
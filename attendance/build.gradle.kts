plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

dependencies {
    // Other modules
    implementation(project(mapOf("path" to ":base")))
    implementation(project(mapOf("path" to ":navigation")))
    implementation(project(mapOf("path" to ":auth")))
    // Dependencies
    coreLibraryDesugaring(AndroidToolDependencies.android_tools_desugar_jdk)
    implementation(AndroidToolDependencies.timber)

    implementation(ComposeDependencies.compose_material3)
    implementation(ComposeDependencies.compose_ui)
    implementation(ComposeDependencies.lifecycle_viewmodel_compose)

    implementation(HiltDependencies.hilt)
    ksp(HiltDependencies.hilt_android_compiler)

    implementation(MaterialDependencies.material_android)

    implementation(NetworkDependencies.moshi)
    ksp(NetworkDependencies.moshi_kotlin_codegen)
    implementation(NetworkDependencies.okhttp)
    implementation(platform(NetworkDependencies.okhttp_bom))
    implementation(NetworkDependencies.retrofit)
    implementation(NetworkDependencies.retrofuture)
    implementation(NetworkDependencies.sandwich)
    implementation(NetworkDependencies.sandwich_retrofit)
    implementation(NetworkDependencies.sandwich_retrofit_serialization)

    implementation(platform(NfcDependencies.nfc_firebase_bom))
    implementation(NfcDependencies.nfc_firebase_analytics) // Firebase BoM and Analytics (f/k/a Core) are required when including TapLinx (line below) manually
    compileOnly(files(NfcDependencies.nxp_nfc_android_aar_path))

    // Test dependencies
    androidTestImplementation(ComposeDependencies.compose_ui_test)

    androidTestImplementation(TestDependencies.junit)
}

android {
    compileSdk = 35
    defaultConfig {
        minSdk = 21
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
        buildConfig = true
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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    namespace = "org.robojackets.apiary.attendance"
    hilt {
        enableExperimentalClasspathAggregation = true
    }
}
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    kotlin("plugin.serialization") version "2.0.0"
    id("org.jetbrains.kotlin.plugin.compose")
}

dependencies {
    // Other modules (none right now)

    // Dependencies
    coreLibraryDesugaring(AndroidToolDependencies.android_tools_desugar_jdk)
    api(AndroidToolDependencies.krate)
    implementation(AndroidToolDependencies.sentry)
    implementation(AndroidToolDependencies.timber)

    implementation(ComposeDependencies.compose_foundation)
    implementation(ComposeDependencies.compose_material3)
    implementation(ComposeDependencies.compose_ui)
    implementation(ComposeDependencies.compose_ui_tooling)
    implementation(ComposeDependencies.compose_material_navigation)

    implementation(HiltDependencies.hilt)
    ksp(HiltDependencies.hilt_android_compiler)

    implementation(MaterialDependencies.material_android)

    implementation(NetworkDependencies.kotlinx_serialization_json)
    implementation(NetworkDependencies.moshi)
    ksp(NetworkDependencies.moshi_kotlin_codegen)
    implementation(NetworkDependencies.retrofit)
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

hilt {
    enableExperimentalClasspathAggregation = true
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

android {
    compileSdk = 36
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
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }
    namespace = "org.robojackets.apiary.base"
    hilt {
        enableExperimentalClasspathAggregation = true
    }
}
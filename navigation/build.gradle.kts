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
    implementation(MaterialDependencies.material_android)
    androidTestImplementation(TestDependencies.junit)

    // UI Tests
    androidTestImplementation(ComposeDependencies.compose_ui_test)
    // Navigation
    api(AndroidXDependencies.androidx_navigation_compose)

    // Hilt
    implementation(HiltDependencies.hilt)
    kapt(HiltDependencies.hilt_android_compiler)

    coreLibraryDesugaring(AndroidToolDependencies.android_tools_desugar_jdk)

    api(ComposeDependencies.compose_runtime)
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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
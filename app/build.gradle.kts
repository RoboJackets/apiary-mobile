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
    implementation(AndroidXDependencies.androidx_appcompat)
    implementation(AndroidXDependencies.androidx_activity_compose)
    androidTestImplementation(TestDependencies.junit)

    // Compose
    implementation(ComposeDependencies.compose_ui)
    implementation(ComposeDependencies.lifecycle_viewmodel_compose)
    implementation(ComposeDependencies.compose_ui_tooling)

    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation(ComposeDependencies.compose_foundation)
    // Material Design
    implementation(ComposeDependencies.compose_material)
    // Material design icons
    implementation(ComposeDependencies.compose_material_icons_core)
    implementation(ComposeDependencies.compose_material_icons_extended)
    // Integration with observables
    implementation(AndroidXDependencies.androidx_activity_compose)

    // Firebase
    implementation(platform(FirebaseDependencies.firebase_bom))

    // NFC
    implementation(files(NfcDependencies.nxp_nfc_android_aar_path))
    implementation(FirebaseDependencies.firebase_core) // Required when including TapLinx (line above) manually

    // UI Tests
    androidTestImplementation(ComposeDependencies.compose_ui_test)
    // Navigation
    implementation(AndroidXDependencies.androidx_navigation_compose)
    // Hilt
    implementation(HiltDependencies.hilt)
    kapt(HiltDependencies.hilt_android_compiler)
    implementation(HiltDependencies.hilt_navigation_compose)

    coreLibraryDesugaring(AndroidToolDependencies.android_tools_desugar_jdk)

    // Settings UI
    implementation(ComposeDependencies.compose_settings)

    // Collects open-source license information
    implementation(AndroidToolDependencies.open_source_licenses)

    // Chrome Custom Tabs
    implementation(AndroidXDependencies.androidx_browser)

    implementation(AuthDependencies.appauth)
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
        versionCode = 3
        versionName = "0.2.0"
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
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
    implementation(project(mapOf("path" to ":attendance")))
    implementation(project(mapOf("path" to ":auth")))
    implementation(project(mapOf("path" to ":base")))
    implementation(project(mapOf("path" to ":navigation")))

    // Dependencies
    coreLibraryDesugaring(AndroidToolDependencies.android_tools_desugar_jdk)
    implementation(AndroidToolDependencies.gson) // For Sentry
    implementation(AndroidToolDependencies.open_source_licenses)
    implementation(AndroidToolDependencies.sentry)
    implementation(AndroidToolDependencies.sentry_timber_tree)
    implementation(AndroidToolDependencies.timber)

    implementation(AndroidXDependencies.androidx_activity_compose)
    implementation(AndroidXDependencies.androidx_appcompat)
    implementation(AndroidXDependencies.androidx_browser)
    implementation(AndroidXDependencies.androidx_navigation_compose)

    implementation(AuthDependencies.appauth)

    implementation(ComposeDependencies.compose_ui)
    implementation(ComposeDependencies.lifecycle_viewmodel_compose)
    implementation(ComposeDependencies.compose_ui_tooling)
    implementation(ComposeDependencies.compose_foundation)
    implementation(ComposeDependencies.compose_material)
    implementation(ComposeDependencies.compose_material_icons_core)
    implementation(ComposeDependencies.compose_material_icons_extended)
    implementation(ComposeDependencies.compose_settings)

    implementation(HiltDependencies.hilt)
    kapt(HiltDependencies.hilt_android_compiler)
    implementation(HiltDependencies.hilt_navigation_compose)

    implementation(NetworkDependencies.moshi_converter_factory)
    implementation(NetworkDependencies.okhttp)
    implementation(platform(NetworkDependencies.okhttp_bom))
    implementation(NetworkDependencies.okhttp_logging_interceptor)
    implementation(NetworkDependencies.retrofit)
    implementation(NetworkDependencies.sandwich) // yum yum

    implementation(platform(NfcDependencies.nfc_firebase_bom))
    implementation(NfcDependencies.nfc_firebase_core) // Firebase BoM and Core are required when including TapLinx (line below) manually
    implementation(files(NfcDependencies.nxp_nfc_android_aar_path))

    // Test dependencies
    androidTestImplementation(ComposeDependencies.compose_ui_test)
    androidTestImplementation(TestDependencies.junit)
}

hilt {
    enableExperimentalClasspathAggregation = true
}

android {
    signingConfigs {
        create("release") {
        }
    }
    compileSdk = 32
    defaultConfig {
        applicationId = "org.robojackets.apiary"
        minSdk = 21
        targetSdk = 32
        versionCode = 12
        versionName = "1.0.0"
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
        kotlinCompilerExtensionVersion = "1.2.0-beta03"
    }
    hilt {
        enableExperimentalClasspathAggregation = true
    }
}
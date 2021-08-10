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
    api(project(mapOf("path" to ":base")))
    api(project(mapOf("path" to ":navigation")))

    implementation(MaterialDependencies.material_android)
    implementation(AndroidXDependencies.androidx_activity_compose)
    androidTestImplementation(TestDependencies.junit)
    androidTestDebugImplementation(TestDependencies.junit)

    // Compose
    implementation(ComposeDependencies.compose_ui)
    implementation(ComposeDependencies.accompanist)
    implementation(AndroidXDependencies.androidx_navigation_compose)

    // Tooling support (Previews, etc.)
    implementation(ComposeDependencies.compose_ui_tooling)
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation(ComposeDependencies.compose_foundation)
    // Material Design
    implementation(ComposeDependencies.compose_material)

    // Integration with observables
    implementation(AndroidXDependencies.androidx_lifecycle_runtime_ktx)

    // UI Tests
    androidTestImplementation(ComposeDependencies.compose_ui_test)
    // Hilt
    implementation(HiltDependencies.hilt)
    kapt(HiltDependencies.hilt_android_compiler)

    // OAuth2
    api(AuthDependencies.appauth)

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
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
    // Other modules
    implementation(project(mapOf("path" to ":base")))
    implementation(project(mapOf("path" to ":navigation")))

    // Dependencies
    coreLibraryDesugaring(AndroidToolDependencies.android_tools_desugar_jdk)

    implementation(AndroidXDependencies.androidx_activity_compose)
    implementation(AndroidXDependencies.androidx_lifecycle_runtime_ktx)
    implementation(AndroidXDependencies.androidx_navigation_compose)

    implementation(AuthDependencies.appauth)

    implementation(ComposeDependencies.accompanist)
    implementation(ComposeDependencies.compose_foundation)
    implementation(ComposeDependencies.compose_material)
    implementation(ComposeDependencies.compose_ui)
    implementation(ComposeDependencies.compose_ui_tooling)

    implementation(HiltDependencies.hilt)
    kapt(HiltDependencies.hilt_android_compiler)

    implementation(MaterialDependencies.material_android)

    // Test dependencies
    androidTestImplementation(ComposeDependencies.compose_ui_test)

    androidTestImplementation(TestDependencies.junit)
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
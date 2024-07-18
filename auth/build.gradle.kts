plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android")
    id("com.google.devtools.ksp")
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
    implementation(AndroidToolDependencies.timber)

    implementation(AndroidXDependencies.androidx_activity_compose)
    implementation(AndroidXDependencies.androidx_lifecycle_runtime)
    implementation(AndroidXDependencies.androidx_navigation_compose)

    implementation(AuthDependencies.appauth)

    implementation(ComposeDependencies.accompanist_systemuicontroller)
    implementation(ComposeDependencies.compose_foundation)
    implementation(ComposeDependencies.compose_material3)
    implementation(ComposeDependencies.compose_ui)
    implementation(ComposeDependencies.compose_ui_tooling)

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
        manifestPlaceholders["appAuthRedirectScheme"] = "org.robojackets.apiary"
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
    namespace = "org.robojackets.apiary.auth"
    hilt {
        enableExperimentalClasspathAggregation = true
    }
}
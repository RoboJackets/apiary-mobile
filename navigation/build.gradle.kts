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

    // Dependencies
    coreLibraryDesugaring(AndroidToolDependencies.android_tools_desugar_jdk)
    implementation(AndroidToolDependencies.timber)

    implementation(AndroidXDependencies.androidx_navigation_compose)

    implementation(HiltDependencies.hilt)
    kapt(HiltDependencies.hilt_android_compiler)

    implementation(MaterialDependencies.material_android)

    // Test dependencies
    androidTestImplementation(TestDependencies.junit)
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    namespace = "org.robojackets.apiary.navigation"
    hilt {
        enableExperimentalClasspathAggregation = true
    }
}
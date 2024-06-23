plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android")
//    kotlin("kapt")
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

    // Dependencies
    coreLibraryDesugaring(AndroidToolDependencies.android_tools_desugar_jdk)
    implementation(AndroidToolDependencies.timber)

    implementation(AndroidXDependencies.androidx_navigation_compose)

    implementation(HiltDependencies.hilt)
    ksp(HiltDependencies.hilt_android_compiler)
//    implementation(HiltDependencies.dagger_producer)

    implementation(MaterialDependencies.material_android)

    // Test dependencies
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    namespace = "org.robojackets.apiary.navigation"
    hilt {
        enableExperimentalClasspathAggregation = true
    }
}
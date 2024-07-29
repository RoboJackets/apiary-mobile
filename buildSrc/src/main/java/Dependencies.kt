object ComposeDependencies {
    object Versions {
        const val accompanist_version = "0.34.0"
        const val compose_settings_version = "2.4.0"
        const val compose_version = "1.6.8"
        const val lifecycle_viewmodel_compose_version = "2.8.2"
        const val compose_material3_version = "1.2.1"
        const val compose_material_navigation_version = "1.7.0-beta01"
    }

    const val accompanist_systemuicontroller =
        "com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist_version}"

    const val compose_foundation =
        "androidx.compose.foundation:foundation:${Versions.compose_version}"

    const val compose_material3 =
        "androidx.compose.material3:material3:${Versions.compose_material3_version}"
    const val compose_material_icons_core =
        "androidx.compose.material:material-icons-core:${Versions.compose_version}"
    const val compose_material_icons_extended =
        "androidx.compose.material:material-icons-extended:${Versions.compose_version}"
    const val compose_material_navigation =
        "androidx.compose.material:material-navigation:${Versions.compose_material_navigation_version}"

    const val compose_settings =
        "com.github.alorma.compose-settings:ui-tiles:${Versions.compose_settings_version}"

    const val compose_ui = "androidx.compose.ui:ui:${Versions.compose_version}"
    const val compose_ui_test = "androidx.compose.ui:ui-test-junit4:${Versions.compose_version}"
    const val compose_ui_tooling = "androidx.compose.ui:ui-tooling:${Versions.compose_version}"

    const val lifecycle_viewmodel_compose =
        "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle_viewmodel_compose_version}"
}

object MaterialDependencies {
    object Versions {
        const val material_android_version = "1.6.1"
    }

    const val material_android =
        "com.google.android.material:material:${Versions.material_android_version}"
}

object AndroidXDependencies {
    object Versions {
        const val androidx_activity_compose_version = "1.9.0"
        const val androidx_appcompat_version = "1.7.0"
        const val androidx_browser_version = "1.8.0"
        const val androidx_lifecycle_runtime_version = "2.8.2"
        const val androidx_navigation_compose_version = "2.7.7"
    }

    const val androidx_activity_compose =
        "androidx.activity:activity-compose:${Versions.androidx_activity_compose_version}"
    const val androidx_appcompat =
        "androidx.appcompat:appcompat:${Versions.androidx_appcompat_version}"
    const val androidx_browser = "androidx.browser:browser:${Versions.androidx_browser_version}"
    const val androidx_lifecycle_runtime =
        "androidx.lifecycle:lifecycle-runtime:${Versions.androidx_lifecycle_runtime_version}"
    const val androidx_navigation_compose =
        "androidx.navigation:navigation-compose:${Versions.androidx_navigation_compose_version}"
}

object FirebaseDependencies {
    object Versions {
        const val firebase_bom_version = "33.1.0"
    }

    const val firebase_bom = "com.google.firebase:firebase-bom:${Versions.firebase_bom_version}"
    const val firebase_analytics = "com.google.firebase:firebase-analytics-ktx" // versioned by BOM
}

object NfcDependencies {
    const val nfc_firebase_bom = FirebaseDependencies.firebase_bom
    const val nfc_firebase_analytics = FirebaseDependencies.firebase_analytics
    const val nxp_nfc_android_aar_path = "../libs/nxpnfcandroidlib-release.aar"
}

object AuthDependencies {
    object Versions {
        const val appauth_version = "0.11.1"
    }

    const val appauth = "net.openid:appauth:${Versions.appauth_version}"
}

object HiltDependencies {
    object Versions {
        const val hilt_navigation_compose_version = "1.2.0"
        const val hilt_version = "2.51.1" // If you update this version, you also need to update
        // the com.google.dagger:hilt-android-gradle-plugin version in build.gradle files
    }

    const val hilt = "com.google.dagger:hilt-android:${Versions.hilt_version}"
    const val hilt_android_compiler =
        "com.google.dagger:hilt-android-compiler:${Versions.hilt_version}"
    const val dagger_producer = "com.google.dagger:dagger-producers:${Versions.hilt_version}"
    const val hilt_navigation_compose =
        "androidx.hilt:hilt-navigation-compose:${Versions.hilt_navigation_compose_version}"
}

object AndroidToolDependencies {
    object Versions {
        const val android_tools_desugar_version = "2.0.4"
        const val krate_version = "2.0.0"
        const val gson_version = "2.11.0"
        const val in_app_update_compose_version = "1.2.0"
        const val open_source_licenses_version = "17.1.0"
        const val sentry_version = "7.10.0"
        const val timber_version = "5.0.1"
    }

    const val android_tools_desugar_jdk =
        "com.android.tools:desugar_jdk_libs:${Versions.android_tools_desugar_version}"
    const val gson = "com.google.code.gson:gson:${Versions.gson_version}"
    const val in_app_update_compose =
        "se.warting.in-app-update:in-app-update-compose-mui:${Versions.in_app_update_compose_version}"
    const val krate = "hu.autsoft:krate:${Versions.krate_version}"
    const val open_source_licenses =
        "com.google.android.gms:play-services-oss-licenses:${Versions.open_source_licenses_version}"
    const val sentry = "io.sentry:sentry-android:${Versions.sentry_version}"
    const val sentry_timber_tree = "io.sentry:sentry-android-timber:${Versions.sentry_version}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber_version}"
}

object NetworkDependencies {
    object Versions {
        const val moshi_version = "1.15.1"
        const val moshi_converter_factory_version = "2.11.0"
        const val kotlinx_serialization_json_version = "1.6.3"
        const val okhttp_bom_version = "4.12.0"
        const val retrofit_version = "2.11.0"
        const val retrofuture_version = "1.7.4"
        const val sandwich_version = "2.0.8"
    }

    const val kotlinx_serialization_json = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinx_serialization_json_version}"
    const val moshi = "com.squareup.moshi:moshi:${Versions.moshi_version}"
    const val moshi_adapters = "com.squareup.moshi:moshi-adapters:${Versions.moshi_version}"
    const val moshi_converter_factory =
        "com.squareup.retrofit2:converter-moshi:${Versions.moshi_converter_factory_version}"
    const val moshi_kotlin_codegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi_version}"
    const val okhttp_bom = "com.squareup.okhttp3:okhttp-bom:${Versions.okhttp_bom_version}"
    const val okhttp = "com.squareup.okhttp3:okhttp"
    const val okhttp_logging_interceptor = "com.squareup.okhttp3:logging-interceptor"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit_version}"
    const val retrofuture = "net.sourceforge.streamsupport:android-retrofuture:${Versions.retrofuture_version}"
    const val sandwich = "com.github.skydoves:sandwich:${Versions.sandwich_version}"
    const val sandwich_retrofit =
        "com.github.skydoves:sandwich-retrofit:${Versions.sandwich_version}"
    const val sandwich_retrofit_serialization =
        "com.github.skydoves:sandwich-retrofit-serialization:${Versions.sandwich_version}"
}

object TestDependencies {
    object Versions {
        const val junit_version = "4.13.2"
    }

    const val junit = "junit:junit:${Versions.junit_version}"
}

object ComposeDependencies {
    object Versions {
        const val accompanist_version = "0.30.1"
        const val compose_settings_version = "0.27.0"
        const val compose_version = "1.4.3"
        const val lifecycle_viewmodel_compose_version = "2.6.1"
    }

    const val accompanist_systemuicontroller =
        "com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist_version}"
    const val accompanist_nav_material =
        "com.google.accompanist:accompanist-navigation-material:${Versions.accompanist_version}"

    const val compose_foundation =
        "androidx.compose.foundation:foundation:${Versions.compose_version}"

    const val compose_material = "androidx.compose.material:material:${Versions.compose_version}"
    const val compose_material_icons_core =
        "androidx.compose.material:material-icons-core:${Versions.compose_version}"
    const val compose_material_icons_extended =
        "androidx.compose.material:material-icons-extended:${Versions.compose_version}"

    const val compose_runtime = "androidx.compose.runtime:runtime:${Versions.compose_version}"
    const val compose_runtime_livedata =
        "androidx.compose.runtime:runtime-livedata:${Versions.compose_version}"

    const val compose_settings =
        "com.github.alorma:compose-settings-ui:${Versions.compose_settings_version}"

    const val compose_ui = "androidx.compose.ui:ui:${Versions.compose_version}"
    const val compose_ui_test = "androidx.compose.ui:ui-test-junit4:${Versions.compose_version}"
    const val compose_ui_tooling = "androidx.compose.ui:ui-tooling:${Versions.compose_version}"

    const val lifecycle_viewmodel_compose =
        "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle_viewmodel_compose_version}"
}

object MaterialDependencies {
    object Versions {
        const val material_android_version = "1.9.0"
    }

    const val material_android =
        "com.google.android.material:material:${Versions.material_android_version}"
}

object AndroidXDependencies {
    object Versions {
        const val androidx_activity_compose_version = "1.7.2"
        const val androidx_appcompat_version = "1.6.1"
        const val androidx_browser_version = "1.5.0"
        const val androidx_lifecycle_runtime_ktx_version = "2.6.1"
        const val androidx_navigation_compose_version = "2.5.3"
    }

    const val androidx_activity_compose =
        "androidx.activity:activity-compose:${Versions.androidx_activity_compose_version}"
    const val androidx_appcompat =
        "androidx.appcompat:appcompat:${Versions.androidx_appcompat_version}"
    const val androidx_browser = "androidx.browser:browser:${Versions.androidx_browser_version}"
    const val androidx_lifecycle_runtime_ktx =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidx_lifecycle_runtime_ktx_version}"
    const val androidx_navigation_compose =
        "androidx.navigation:navigation-compose:${Versions.androidx_navigation_compose_version}"
}

object FirebaseDependencies {
    object Versions {
        const val firebase_bom_version = "32.0.0"
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
        const val hilt_navigation_compose_version = "1.0.0"
        const val hilt_version = "2.44"
    }

    const val hilt = "com.google.dagger:hilt-android:${Versions.hilt_version}"
    const val hilt_android_compiler =
        "com.google.dagger:hilt-android-compiler:${Versions.hilt_version}"
    const val hilt_navigation_compose =
        "androidx.hilt:hilt-navigation-compose:${Versions.hilt_navigation_compose_version}"
}

object AndroidToolDependencies {
    object Versions {
        const val android_tools_desugar_version = "1.1.5"
        const val krate_version = "2.0.0"
        const val gson_version = "2.10.1"
        const val in_app_update_compose_version = "0.0.17"
        const val open_source_licenses_version = "17.0.1"
        const val sentry_version = "6.19.1"
        const val timber_version = "5.0.1"
    }

    const val android_tools_desugar_jdk =
        "com.android.tools:desugar_jdk_libs:${Versions.android_tools_desugar_version}"
    const val gson = "com.google.code.gson:gson:${Versions.gson_version}"
    const val in_app_update_compose =
        "se.warting.in-app-update:in-app-update-compose:${Versions.in_app_update_compose_version}"
    const val krate = "hu.autsoft:krate:${Versions.krate_version}"
    const val open_source_licenses =
        "com.google.android.gms:play-services-oss-licenses:${Versions.open_source_licenses_version}"
    const val sentry = "io.sentry:sentry-android:${Versions.sentry_version}"
    const val sentry_timber_tree = "io.sentry:sentry-android-timber:${Versions.sentry_version}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber_version}"
}

object NetworkDependencies {
    object Versions {
        const val moshi_version = "1.15.0"
        const val moshi_converter_factory_version = "2.9.0"
        const val okhttp_bom_version = "4.11.0"
        const val retrofit_version = "2.9.0"
        const val retrofuture_version = "1.7.4"
        const val sandwich_version = "1.3.6"
    }

    const val moshi = "com.squareup.moshi:moshi:${Versions.moshi_version}"
    const val moshi_converter_factory =
        "com.squareup.retrofit2:converter-moshi:${Versions.moshi_converter_factory_version}"
    const val moshi_kotlin_codegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi_version}"
    const val okhttp_bom = "com.squareup.okhttp3:okhttp-bom:${Versions.okhttp_bom_version}"
    const val okhttp = "com.squareup.okhttp3:okhttp"
    const val okhttp_logging_interceptor = "com.squareup.okhttp3:logging-interceptor"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit_version}"
    const val retrofuture = "net.sourceforge.streamsupport:android-retrofuture:${Versions.retrofuture_version}"
    const val sandwich = "com.github.skydoves:sandwich:${Versions.sandwich_version}"
}

object TestDependencies {
    object Versions {
        const val junit_version = "4.13.2"
    }

    const val junit = "junit:junit:${Versions.junit_version}"
}

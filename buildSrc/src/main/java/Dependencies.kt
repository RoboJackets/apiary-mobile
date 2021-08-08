object ComposeDependencies {
    object Versions {
        const val compose_version = "1.0.0"
        const val lifecycle_viewmodel_compose_version = "1.0.0-alpha07"
        const val accompanist_version = "0.12.0"
        const val compose_settings_version = "0.2.0"
    }

    const val compose_ui = "androidx.compose.ui:ui:${Versions.compose_version}"
    const val lifecycle_viewmodel_compose =
        "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle_viewmodel_compose_version}"
    const val accompanist =
        "com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist_version}"

    const val compose_ui_tooling = "androidx.compose.ui:ui-tooling:${Versions.compose_version}"
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

    const val compose_ui_test = "androidx.compose.ui:ui-test-junit4:${Versions.compose_version}"

    const val compose_settings =
        "com.github.alorma:compose-settings:${Versions.compose_settings_version}"
}

object MaterialDependencies {
    object Versions {
        const val material_android_version = "1.4.0"
    }

    const val material_android =
        "com.google.android.material:material:${Versions.material_android_version}"
}

object AndroidXDependencies {
    object Versions {
        const val androidx_lifecycle_runtime_ktx_version = "2.3.1"
        const val androidx_activity_compose_version = "1.3.1"
        const val androidx_navigation_compose_version = "2.4.0-alpha05"
        const val androidx_preference_ktx_version = "1.1.1"
        const val androidx_browser_version = "1.3.0"
        const val androidx_appcompat_version = "1.3.1"
    }

    const val androidx_lifecycle_runtime_ktx =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidx_lifecycle_runtime_ktx_version}"
    const val androidx_activity_compose =
        "androidx.activity:activity-compose:${Versions.androidx_activity_compose_version}"
    const val androidx_navigation_compose =
        "androidx.navigation:navigation-compose:${Versions.androidx_navigation_compose_version}"
    const val androidx_preference_ktx =
        "androidx.preference:preference-ktx:${Versions.androidx_preference_ktx_version}"
    const val androidx_browser = "androidx.browser:browser:${Versions.androidx_browser_version}"
    const val androidx_appcompat =
        "androidx.appcompat:appcompat:${Versions.androidx_appcompat_version}"
}

object FirebaseDependencies {
    object Versions {
        const val firebase_bom_version = "28.3.0"
    }

    const val firebase_bom = "com.google.firebase:firebase-bom:${Versions.firebase_bom_version}"
    const val firebase_core = "com.google.firebase:firebase-core" // No explicit version specified
    // because of the inclusion of the Firebase BOM
}

object NfcDependencies {
    const val nxp_nfc_android_aar_path = "../libs/nxpnfcandroidlib-release.aar"
}

object AuthDependencies {
    object Versions {
        const val appauth_version = "0.9.0"
    }

    const val appauth = "net.openid:appauth:${Versions.appauth_version}"
}

object HiltDependencies {
    object Versions {
        const val hilt_version = "2.35"
        const val hilt_navigation_compose_version = "1.0.0-alpha03"
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
        const val open_source_licenses_version = "17.0.0"
        const val krate_version = "1.1.0"
    }

    const val android_tools_desugar_jdk =
        "com.android.tools:desugar_jdk_libs:${Versions.android_tools_desugar_version}"
    const val open_source_licenses =
        "com.google.android.gms:play-services-oss-licenses:${Versions.open_source_licenses_version}"
    const val krate = "hu.autsoft:krate:${Versions.krate_version}"
}

object TestDependencies {
    object Versions {
        const val junit_version = "4.13.2"
    }

    const val junit = "junit:junit:${Versions.junit_version}"
}

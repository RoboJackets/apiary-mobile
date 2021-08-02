package org.robojackets.apiary.ui.settings

import android.content.ComponentName
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.robojackets.apiary.base.GlobalSettings
import org.robojackets.apiary.navigation.NavigationDirections
import org.robojackets.apiary.navigation.NavigationManager
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val globalSettings: GlobalSettings,
    val navigationManager: NavigationManager,
) : ViewModel() {
    val privacyPolicyUrl: Uri = Uri.withAppendedPath(globalSettings.appEnv.apiBaseUrl, "privacy")
    var customTabsClient: CustomTabsClient? = null

    val customTabsServiceConnection = object : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(
            name: ComponentName,
            client: CustomTabsClient
        ) {
            customTabsClient = client
            Log.d("SettingsViewModel", "Custom tabs warmup")
            val session = customTabsClient?.newSession(null)
            customTabsClient?.warmup(0) // Flags is "reserved for future use"
            Log.d("SettingsViewModel", "session: $session")
            session?.mayLaunchUrl(privacyPolicyUrl, null, null)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            customTabsClient = null
        }
    }

    private fun navigateToLogin() {
        navigationManager.navigate(NavigationDirections.Authentication)
    }

    fun logout() {
        globalSettings.clearLoginInfo()
        navigateToLogin()
    }

    fun getPrivacyPolicyCustomTabsIntent(toolbarColor: Int): CustomTabsIntent {
        val customTabsBuilder = CustomTabsIntent.Builder()

        customTabsBuilder.setDefaultColorSchemeParams(
            CustomTabColorSchemeParams.Builder()
                .setToolbarColor(toolbarColor).build()
        )

        return customTabsBuilder.build()
    }
}
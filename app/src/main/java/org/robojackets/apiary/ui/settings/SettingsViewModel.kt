package org.robojackets.apiary.ui.settings

import android.content.ComponentName
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.robojackets.apiary.auth.AuthStateManager
import org.robojackets.apiary.auth.model.UserInfo
import org.robojackets.apiary.auth.network.UserRepository
import org.robojackets.apiary.base.GlobalSettings
import org.robojackets.apiary.base.model.NetworkResult
import org.robojackets.apiary.base.model.ServerInfoContainer
import org.robojackets.apiary.base.repository.ServerInfoRepository
import org.robojackets.apiary.base.ui.theme.webNavBarBackground
import org.robojackets.apiary.navigation.NavigationDirections
import org.robojackets.apiary.navigation.NavigationManager
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val globalSettings: GlobalSettings,
    val navigationManager: NavigationManager,
    val serverInfoRepository: ServerInfoRepository,
    val userRepository: UserRepository,
    val authStateManager: AuthStateManager,
) : ViewModel() {
    val privacyPolicyUrl: Uri = Uri.withAppendedPath(globalSettings.appEnv.apiBaseUrl, "privacy")
    val makeAWishUrl: Uri = Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSelERsYq3" +
            "gLmHbWvVCWha5iCU8z3r9VYC0hCN4ArLpMAiysaQ/viewform?entry.1338203640=MyRoboJackets%20" +
            "Android")
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

    private val _state = MutableStateFlow(SettingsState())

    private val user = MutableStateFlow<UserInfo?>(null)

    val state: StateFlow<SettingsState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(listOf(
                user,
            )) {
                flows -> SettingsState(
                    flows[0] as UserInfo?
                )
            }.catch { throwable -> throw throwable }
                .collect { _state.value = it }
        }
    }

    private fun navigateToLogin() {
        navigationManager.navigate(NavigationDirections.Authentication)
    }

    fun logout() {
        authStateManager.replace(null)
        globalSettings.clearLoginInfo()
        navigateToLogin()
    }

    fun getCustomTabsIntent(toolbarColor: Int = webNavBarBackground.toArgb()): CustomTabsIntent {
        val customTabsBuilder = CustomTabsIntent.Builder()

        customTabsBuilder.setDefaultColorSchemeParams(
            CustomTabColorSchemeParams.Builder()
                .setToolbarColor(toolbarColor).build()
        )

        return customTabsBuilder.build()
    }

    fun getServerInfo() {
        viewModelScope.launch {
            val serverInfoContainerResult: NetworkResult<ServerInfoContainer> = serverInfoRepository.getServerInfo()

            when (serverInfoContainerResult) {
                is NetworkResult.Success -> {
                    Log.d("SettingsViewModel", "Server info result: ${serverInfoContainerResult.data?.info?.appName}")
                }
                is NetworkResult.Error -> {
                    Log.d("SettingsViewModel", "Server info call failed: ${serverInfoContainerResult.message}")
                }
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            if (user.value == null) {
                user.value = userRepository.getLoggedInUserInfo()?.user
            }
        }
    }
}

data class SettingsState(
    val user: UserInfo? = null,
)
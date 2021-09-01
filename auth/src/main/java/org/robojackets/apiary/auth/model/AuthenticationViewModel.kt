package org.robojackets.apiary.auth.model

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse
import org.robojackets.apiary.auth.AuthStateManager
import org.robojackets.apiary.auth.model.LoginStatus.ERROR
import org.robojackets.apiary.auth.model.LoginStatus.NOT_STARTED
import org.robojackets.apiary.base.AppEnvironment
import org.robojackets.apiary.base.GlobalSettings
import org.robojackets.apiary.navigation.NavigationActions
import org.robojackets.apiary.navigation.NavigationManager
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    @Suppress("UnusedPrivateMember") val savedStateHandle: SavedStateHandle,
    val navigationManager: NavigationManager,
    val globalSettings: GlobalSettings,
    val authStateManager: AuthStateManager,
) : ViewModel() {
    companion object {
        const val TAG = "AuthenticationViewModel"
    }

    private val _state = MutableStateFlow(AuthenticationState())
    private val appEnv = MutableStateFlow(globalSettings.appEnv)
    private val loginStatus = MutableStateFlow(NOT_STARTED)
    private val loginErrorMessage = MutableStateFlow<String?>(null)

    val state: StateFlow<AuthenticationState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    appEnv,
                    loginStatus,
                    loginErrorMessage,
                )
            ) { flows ->
                AuthenticationState(
                    appEnv = flows[0] as AppEnvironment,
                    loginStatus = flows[1] as LoginStatus,
                    loginErrorMessage = flows[2] as String?,
                )
            }
                .catch { throwable -> throw throwable }
                .collect { _state.value = it }
        }
    }

    fun navigateToAttendance() {
        navigationManager.navigate(NavigationActions.Attendance.authToAttendance())
        return
    }

    fun setAppEnv(envName: String) {
        globalSettings.appEnvName = envName
        appEnv.value = AppEnvironment.valueOf(envName)
    }

    fun validateAuthInfo(accessToken: String?, refreshToken: String?): Boolean {
        return accessToken?.isNotEmpty() == true && refreshToken?.isNotEmpty() == true
    }

    fun saveAuthInfo(accessToken: String, refreshToken: String) {
        globalSettings.accessToken = accessToken
        globalSettings.refreshToken = refreshToken
    }

    fun recordAuthError(error: AuthorizationException?) {
        Log.e(TAG, "An authentication error was caught", error)
        recordAuthError(error?.errorDescription ?: "Unknown error")
    }

    fun recordAuthError(errorMessage: String) {
        Log.i(TAG, "Recording auth error: $errorMessage")
        loginStatus.value = ERROR
        loginErrorMessage.value = errorMessage
    }

    fun setLoginStatus(status: LoginStatus) {
        loginStatus.value = status
    }

    fun updateAuthStateAfterAuthorization(
        response: AuthorizationResponse?,
        ex: AuthorizationException?
    ) {
        authStateManager.updateAfterAuthorization(response, ex)
    }

    fun updateAuthStateAfterTokenResponse(
        response: TokenResponse?,
        ex: AuthorizationException?
    ) {
        authStateManager.updateAfterTokenResponse(response, ex)
    }
}

data class AuthenticationState(
    val appEnv: AppEnvironment = AppEnvironment.Production,
    val loginStatus: LoginStatus = NOT_STARTED,
    val loginErrorMessage: String? = null
)

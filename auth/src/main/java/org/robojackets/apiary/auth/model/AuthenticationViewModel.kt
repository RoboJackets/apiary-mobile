package org.robojackets.apiary.auth.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.robojackets.apiary.base.AppEnvironment
import org.robojackets.apiary.base.GlobalSettings
import org.robojackets.apiary.navigation.NavigationDirections
import org.robojackets.apiary.navigation.NavigationManager
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    @Suppress("UnusedPrivateMember") private val savedStateHandle: SavedStateHandle,
    private val navigationManager: NavigationManager,
    private val globalSettings: GlobalSettings,
) : ViewModel() {
    private val _state = MutableStateFlow(AuthenticationState())

    private val loading = MutableStateFlow(false)
    private val appEnv = MutableStateFlow(globalSettings.appEnv)

    val state: StateFlow<AuthenticationState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    loading,
                    appEnv
                )
            ) { flows ->
                AuthenticationState(
                    loading = flows[0] as Boolean,
                    appEnv = flows[1] as AppEnvironment,
                )
            }
                .catch { throwable -> throw throwable }
                .collect { _state.value = it }
        }
    }

    fun navigateToAttendance() {
        navigationManager.navigate(NavigationDirections.Attendance)
        return
    }

    fun setAppEnv(envName: String) {
        globalSettings.appEnvName = envName
        appEnv.value = AppEnvironment.valueOf(envName)
    }
}

data class AuthenticationState(
    val loading: Boolean = false,
    val appEnv: AppEnvironment = AppEnvironment.Production,
)

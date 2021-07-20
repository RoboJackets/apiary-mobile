package org.robojackets.apiary.auth.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.robojackets.apiary.navigation.NavigationDirections
import org.robojackets.apiary.navigation.NavigationManager
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val navigationManager: NavigationManager,
): ViewModel() {
    private val _state = MutableStateFlow(AuthenticationState())

    private val loading = MutableStateFlow(false)

    val state: StateFlow<AuthenticationState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(listOf(
                loading,
            )) {
                    flows -> AuthenticationState(
                flows[0]
            )
            }
                .catch { throwable -> throw throwable }
                .collect { _state.value = it }
        }
    }

    fun navigateToAttendance() {
        navigationManager.navigate(NavigationDirections.Attendance)
    }
}

data class AuthenticationState(
    val loading: Boolean = false
)
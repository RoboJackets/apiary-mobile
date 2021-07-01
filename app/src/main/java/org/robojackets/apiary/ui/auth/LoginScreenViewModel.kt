package org.robojackets.apiary.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {
    companion object {
        val TAG = this::class.simpleName
    }

    private val _state = MutableStateFlow(LoginScreenState())

    private val loading = MutableStateFlow(false)
    private val loginError = MutableStateFlow<String?>(null)

    val state: StateFlow<LoginScreenState>
        get() = _state

    init {
        Log.i(TAG, "LoginScreenViewModel initialized")

        viewModelScope.launch {
            combine(listOf(
                loading,
                loginError
            )) { flows -> LoginScreenState(
                loading = flows[0] as Boolean,
                loginError = flows[1] as String?
            )
            }.catch { throwable -> throw throwable }
                .collect { _state.value = it }
        }
    }


}

data class LoginScreenState(
    var loading: Boolean = false,
    val loginError: String? = null
)
package org.robojackets.apiary.attendance.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _state = MutableStateFlow(AttendanceState())

    private val loading = MutableStateFlow(false)

    val state: StateFlow<AttendanceState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(listOf(
                loading,
            )) {
                flows -> AttendanceState(
                    flows[0]
                )
            }
                .catch { throwable -> throw throwable }
                .collect { _state.value = it }
        }
    }
}

data class AttendanceState(
    val loading: Boolean = false
)
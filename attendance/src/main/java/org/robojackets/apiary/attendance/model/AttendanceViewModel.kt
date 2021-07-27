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
    @Suppress("UnusedPrivateMember") private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _state = MutableStateFlow(AttendanceState())

    private val lastGtid = MutableStateFlow("")

    val state: StateFlow<AttendanceState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(listOf(
                lastGtid,
            )) {
                flows -> AttendanceState(
                    flows[0]
                )
            }
                .catch { throwable -> throw throwable }
                .collect { _state.value = it }
        }
    }

    fun updateGtid(gtid: String) {
        lastGtid.value = gtid
    }
}

data class AttendanceState(
    val lastGtid: String = ""
)

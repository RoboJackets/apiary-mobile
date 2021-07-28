package org.robojackets.apiary.attendance.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.robojackets.apiary.attendance.model.AttendanceScreenState.LOADING
import org.robojackets.apiary.attendance.model.AttendanceScreenState.READY_TO_SCAN
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    @Suppress("UnusedPrivateMember") private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _state = MutableStateFlow(AttendanceState())

    private val lastAttendee = MutableStateFlow<AttendanceStoreResult?>(null)
    private val screenState = MutableStateFlow(READY_TO_SCAN)
    private val totalScans = MutableStateFlow(0)

    val state: StateFlow<AttendanceState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(listOf(
                lastAttendee,
                screenState,
                totalScans,
            )) {
                flows -> AttendanceState(
                    flows[0] as AttendanceStoreResult?,
                    flows[1] as AttendanceScreenState,
                    flows[2] as Int,
                )
            }
                .catch { throwable -> throw throwable }
                .collect { _state.value = it }
        }
    }

    fun recordScan(tap: BuzzCardTap) {
        screenState.value = LOADING

        viewModelScope.launch {
            delay(3000)

            lastAttendee.value = AttendanceStoreResult(
                tap = tap,
                name = "George P. Burdell"
            )
            screenState.value = READY_TO_SCAN

        }
    }
}

data class AttendanceState(
    val lastAttendee: AttendanceStoreResult? = null,
    val screenState: AttendanceScreenState = READY_TO_SCAN,
    val totalScans: Int = 0,
)

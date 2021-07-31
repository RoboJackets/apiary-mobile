package org.robojackets.apiary.attendance.model

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.robojackets.apiary.attendance.model.AttendanceScreenState.Loading
import org.robojackets.apiary.attendance.model.AttendanceScreenState.ReadyForTap
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    @Suppress("UnusedPrivateMember") private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _state = MutableStateFlow(AttendanceState())

    private val lastAttendee = MutableStateFlow<AttendanceStoreResult?>(null)
    private val screenState = MutableStateFlow(ReadyForTap)
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
        if (screenState.value == Loading) {
            Log.d("AttendanceScreen", "Ignoring BuzzCard tap because another one is currently being processed")
            return
        }

        screenState.value = Loading

        viewModelScope.launch {
            delay(250)

            lastAttendee.value = AttendanceStoreResult(
                tap = tap,
                name = "George P. Burdell"
            )
            screenState.value = ReadyForTap
            totalScans.value += 1

        }
    }
}

data class AttendanceState(
    val lastAttendee: AttendanceStoreResult? = null,
    val screenState: AttendanceScreenState = ReadyForTap,
    val totalScans: Int = 0,
)

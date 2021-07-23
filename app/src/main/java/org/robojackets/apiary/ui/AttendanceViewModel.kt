package org.robojackets.apiary.ui

import android.util.Log
import androidx.lifecycle.ViewModel

class AttendanceViewModel : ViewModel() {
    companion object {
        const val TAG = "AttendanceViewModel"
//    }
//    private val _state = MutableStateFlow(AttendanceScreenState())
//
//
//    val state: StateFlow<AttendanceScreenState>
//        get() = _state

        init {
            Log.i(TAG, "AttendanceViewModel initialized")

//        viewModelScope.launch {
//            combine(listOf(
//            )) {
//                flows -> AttendanceScreenState(
//                )
//            }.catch { throwable -> throw throwable }
//                .collect { _state.value = it }
        }
    }
}

// data class AttendanceScreenState(
//
// )

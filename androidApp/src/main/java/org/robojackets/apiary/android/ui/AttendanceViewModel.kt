package org.robojackets.apiary.android.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.robojackets.apiary.SpaceXSDK
import org.robojackets.apiary.entity.RocketLaunch

class AttendanceViewModel: ViewModel() {
    companion object {
        const val TAG = "AttendanceViewModel"
    }
    val sdk = SpaceXSDK()
    private val _state = MutableStateFlow(AttendanceScreenState())

    private val launches = MutableStateFlow<List<RocketLaunch>>(emptyList())

    val state: StateFlow<AttendanceScreenState>
        get() = _state

    init {
        Log.i(TAG, "AttendanceViewModel initialized")

        viewModelScope.launch {
            combine(listOf(
                launches
            )) {
                flows -> AttendanceScreenState(
                    launches = flows[0]
                )
            }.catch { throwable -> throw throwable }
                .collect { _state.value = it }
        }
    }

    fun getLaunches() {
        viewModelScope.launch {
            launches.value = sdk.getLaunches()
        }
    }
}

data class AttendanceScreenState(
    val launches: List<RocketLaunch> = emptyList()
)
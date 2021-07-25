package org.robojackets.apiary.attendance

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.robojackets.apiary.attendance.model.AttendanceState
import org.robojackets.apiary.attendance.model.AttendanceViewModel

@Composable
private fun Attendance(
    @Suppress("UnusedPrivateMember") viewState: AttendanceState
) {
    Text("Attendance")
}

@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel
) {
    val state by viewModel.state.collectAsState()
    Attendance(state)
}

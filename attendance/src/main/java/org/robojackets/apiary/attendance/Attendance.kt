package org.robojackets.apiary.attendance

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nxp.nfclib.NxpNfcLib
import org.robojackets.apiary.attendance.model.AttendanceState
import org.robojackets.apiary.attendance.model.AttendanceViewModel
import org.robojackets.apiary.base.ui.nfc.BuzzCardPrompt


@Composable
private fun Attendance(
    @Suppress("UnusedPrivateMember") viewState: AttendanceState,
    nfcLib: NxpNfcLib,
    viewModel: AttendanceViewModel
) {
    fun onGtidUpdated(it: String) {
        viewModel.updateGtid(it)
    }

    Text("Last GTID: ${viewState.lastGtid}")
    BuzzCardPrompt(nfcLib = nfcLib, ::onGtidUpdated)

}

@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel,
    nfcLib: NxpNfcLib,
) {
    val state by viewModel.state.collectAsState()
    Attendance(state, nfcLib, viewModel)
}

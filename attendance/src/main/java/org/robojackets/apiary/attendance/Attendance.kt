package org.robojackets.apiary.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.nxp.nfclib.NxpNfcLib
import org.robojackets.apiary.attendance.model.AttendanceScreenState.Loading
import org.robojackets.apiary.attendance.model.AttendanceState
import org.robojackets.apiary.attendance.model.AttendanceViewModel
import org.robojackets.apiary.base.ui.nfc.BuzzCardPrompt
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap

@Composable
private fun Attendance(
    @Suppress("UnusedPrivateMember") viewState: AttendanceState,
    nfcLib: NxpNfcLib,
    onBuzzcardTap: (buzzcardTap: BuzzCardTap) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxHeight().fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Text("Recording attendance for RoboNav")
            Text("Last attendee: ${viewState.lastAttendee?.name ?: "None"}")
            Text("🔥 5 attendees recorded. You're on a roll!")
        }

        BuzzCardPrompt(hidePrompt = viewState.screenState == Loading, nfcLib = nfcLib, onBuzzCardTap = onBuzzcardTap)

        if (viewState.screenState == Loading) {
            Text("Processing tap...")
        }

        Text(
            with(AnnotatedString.Builder()) {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("Total attendees: ")
                pop()
                append(viewState.totalAttendees.toString())
                toAnnotatedString()
            },
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel,
    nfcLib: NxpNfcLib,
) {
    val state by viewModel.state.collectAsState()
    Attendance(
        state,
        nfcLib,
        onBuzzcardTap = {
            viewModel.recordScan(it)
        }
    )
}

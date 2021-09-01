package org.robojackets.apiary.attendance

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nxp.nfclib.NxpNfcLib
import org.robojackets.apiary.attendance.model.AttendanceScreenState.Loading
import org.robojackets.apiary.attendance.model.AttendanceState
import org.robojackets.apiary.attendance.model.AttendanceViewModel
import org.robojackets.apiary.base.model.Attendable
import org.robojackets.apiary.base.model.AttendableType
import org.robojackets.apiary.base.ui.nfc.BuzzCardPrompt
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.base.ui.util.ContentPadding

@Composable
private fun Attendance(
    @Suppress("UnusedPrivateMember") viewState: AttendanceState,
    nfcLib: NxpNfcLib,
    attendable: Attendable,
    onBuzzcardTap: (buzzcardTap: BuzzCardTap) -> Unit,
    onNavigateToAttendableSelection: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.fillMaxWidth()) {
            Text("Recording attendance for ${viewState.selectedAttendable?.type} ${viewState.selectedAttendable?.id}")
            Text("Last attendee: ${viewState.lastAttendee?.name ?: "None"}")
//            Text("🔥 5 attendees recorded. You're on a roll!")
            Button(onClick = {
                onNavigateToAttendableSelection()
            }, Modifier.align(CenterHorizontally).padding(top = 8.dp)) {
                Text("Change team or event")
            }
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
    attendableType: AttendableType,
    attendableId: Int,
) {
    LaunchedEffect(attendableType, attendableId) {
        viewModel.saveAttendableSelectionToState(
            Attendable(
                attendableId,
                "",
                "",
                attendableType
            )
        )
//        viewModel.getAttendableInfo(attendableType, attendableId)
    }

    val state by viewModel.state.collectAsState()
    ContentPadding {
        Attendance(
            state,
            nfcLib,
            attendable = Attendable(
                attendableId,
                "",
                "",
                attendableType
            ),
            onBuzzcardTap = {
                viewModel.recordScan(it)
            },
            onNavigateToAttendableSelection = {
                viewModel.navigateToAttendableSelection()
            }
        )
    }
}

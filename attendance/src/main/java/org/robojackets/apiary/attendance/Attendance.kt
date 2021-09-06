package org.robojackets.apiary.attendance

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nxp.nfclib.NxpNfcLib
import org.robojackets.apiary.attendance.model.AttendanceScreenState.Loading
import org.robojackets.apiary.attendance.model.AttendanceState
import org.robojackets.apiary.attendance.model.AttendanceViewModel
import org.robojackets.apiary.base.model.AttendableType
import org.robojackets.apiary.base.ui.ActionPrompt
import org.robojackets.apiary.base.ui.icons.PendingIcon
import org.robojackets.apiary.base.ui.nfc.BuzzCardPrompt
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.base.ui.util.ContentPadding

@Composable
private fun Attendance(
    viewState: AttendanceState,
    nfcLib: NxpNfcLib,
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
            Row(
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Recording attendance for ${viewState.selectedAttendable?.name}")
                TextButton(
                    onClick = {
                        onNavigateToAttendableSelection()
                    },
                ) {
                    Text("Change")
                }
            }
        }

        Text("Last attendee: ${viewState.lastAttendee?.name ?: "None"}")

        when (viewState.totalAttendees) {
            5 -> Text("🔥 5 attendees recorded. You're on a roll!")
            10 -> Text("👑 10 attendees. You're awesome!")
            25 -> Text("🎸 25 attendees! You're a rockstar!")
            50 -> Text("🎉 50 attendees! Is this GI?")
            100 -> Text("💯 100 ATTENDEES! Go give yourself a prize!")
        }

        BuzzCardPrompt(
            hidePrompt = viewState.screenState == Loading,
            nfcLib = nfcLib,
            onBuzzCardTap = onBuzzcardTap
        )

        if (viewState.screenState == Loading) {
            ActionPrompt(icon = { PendingIcon(Modifier.size(114.dp)) }, title = "Processing...")
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
        viewModel.getAttendableInfo(attendableType, attendableId)
    }

    val state by viewModel.state.collectAsState()
    ContentPadding {
        Attendance(
            state,
            nfcLib,
            onBuzzcardTap = {
                viewModel.recordScan(it)
            }
        ) {
            viewModel.navigateToAttendableSelection()
        }
    }
}

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nxp.nfclib.NxpNfcLib
import org.robojackets.apiary.attendance.model.AttendanceScreenState.*
import org.robojackets.apiary.attendance.model.AttendanceState
import org.robojackets.apiary.attendance.model.AttendanceViewModel
import org.robojackets.apiary.base.model.AttendableType
import org.robojackets.apiary.base.ui.ActionPrompt
import org.robojackets.apiary.base.ui.IconWithText
import org.robojackets.apiary.base.ui.icons.PendingIcon
import org.robojackets.apiary.base.ui.icons.WarningIcon
import org.robojackets.apiary.base.ui.nfc.BuzzCardPrompt
import org.robojackets.apiary.base.ui.nfc.BuzzCardPromptExternalError
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.base.ui.theme.danger
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.base.ui.util.LoadingSpinner

private fun getExternalError(error: String?): BuzzCardPromptExternalError? {
    error?.let {
        return BuzzCardPromptExternalError("Unable to save data", it)
    }

    return null
}

@Suppress("LongMethod", "MagicNumber")
@Composable
private fun Attendance(
    viewState: AttendanceState,
    nfcLib: NxpNfcLib,
    onBuzzcardTap: (buzzcardTap: BuzzCardTap) -> Unit,
    onNavigateToAttendableSelection: () -> Unit,
) {

    if (viewState.selectedAttendable == null) {
        LoadingSpinner()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.fillMaxWidth()) {
            Text("Recording attendance for ${viewState.selectedAttendable.name}")
            Text("Last attendee: ${viewState.lastAttendee?.name ?: "None"}")

            Button(
                onClick = {
                    onNavigateToAttendableSelection()
                },
                Modifier
                    .align(CenterHorizontally)
                    .padding(top = 8.dp)
            ) {
                Text("Change team or event")
            }

            when (viewState.totalAttendees) {
                5 -> Text("🔥 5 attendees recorded. You're on a roll!")
                10 -> Text("👑 10 attendees. You're awesome!")
                25 -> Text("🎸 25 attendees! You're a rockstar!")
                42 -> Text("4️⃣2️⃣ The meaning of life.")
                50 -> Text("🎉 50 attendees! Is this GI?")
                100 -> Text("💯 100 ATTENDEES! Go give yourself a prize!")
            }
        }

        BuzzCardPrompt(
            hidePrompt = viewState.screenState != ReadyForTap,
            nfcLib = nfcLib,
            onBuzzCardTap = onBuzzcardTap,
            externalError = getExternalError(viewState.error)
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
        if (state.selectedAttendable == null && state.error != null) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                IconWithText(
                    { WarningIcon(tint = danger) },
                    state.error ?: "An unknown error occurred",
                    TextAlign.Center
                )
                Button(onClick = {
                    viewModel.getAttendableInfo(attendableType, attendableId)
                }, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Retry")
                }
            }
        } else {
            Attendance(
                state,
                nfcLib,
                onBuzzcardTap = {
                    viewModel.recordScan(it)
                },
                onNavigateToAttendableSelection = {
                    viewModel.navigateToAttendableSelection()
                }
            )
        }
    }
}

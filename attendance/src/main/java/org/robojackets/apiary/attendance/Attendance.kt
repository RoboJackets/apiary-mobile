package org.robojackets.apiary.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nxp.nfclib.NxpNfcLib
import org.robojackets.apiary.attendance.model.AttendanceScreenState.Loading
import org.robojackets.apiary.attendance.model.AttendanceScreenState.ReadyForTap
import org.robojackets.apiary.attendance.model.AttendanceState
import org.robojackets.apiary.attendance.model.AttendanceViewModel
import org.robojackets.apiary.attendance.ui.AttendeeCountCelebration
import org.robojackets.apiary.base.model.AttendableType
import org.robojackets.apiary.base.ui.ActionPrompt
import org.robojackets.apiary.base.ui.CurrentlySelectedItem
import org.robojackets.apiary.base.ui.IconWithText
import org.robojackets.apiary.base.ui.bluetooth.Mrd5Manager
import org.robojackets.apiary.base.ui.icons.AccountCircleIcon
import org.robojackets.apiary.base.ui.icons.EventIcon
import org.robojackets.apiary.base.ui.icons.GroupsIcon
import org.robojackets.apiary.base.ui.icons.PendingIcon
import org.robojackets.apiary.base.ui.icons.WarningIcon
import org.robojackets.apiary.base.ui.nfc.BuzzCardPrompt
import org.robojackets.apiary.base.ui.nfc.BuzzCardPromptExternalError
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.base.ui.theme.danger
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.base.ui.util.IconRow
import org.robojackets.apiary.base.ui.util.LoadingSpinner

private fun getExternalError(error: String?): BuzzCardPromptExternalError? {
    error?.let {
        return BuzzCardPromptExternalError("Unable to save data", it)
    }

    return null
}

private fun getAttendableIcon(attendableType: AttendableType): @Composable () -> Unit = {
    when (attendableType) {
        AttendableType.Team -> { GroupsIcon() }
        AttendableType.Event -> { EventIcon() }
    }
}

@Suppress("LongMethod", "MagicNumber")
@Composable
private fun Attendance(
    viewState: AttendanceState,
    nfcLib: NxpNfcLib,
    mrd5Manager: Mrd5Manager,
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
            Text("Record attendance", style = MaterialTheme.typography.headlineSmall)
            CurrentlySelectedItem(
                name = viewState.selectedAttendable.name,
                icon = getAttendableIcon(viewState.selectedAttendable.type),
                onChangeItem = onNavigateToAttendableSelection
            )
            IconRow(
                icon = { AccountCircleIcon() },
                text = "Last attendee: ${viewState.lastAttendee?.name ?: "None"}",
                button = {}
            )
            AttendeeCountCelebration(viewState.totalAttendees)
        }

        BuzzCardPrompt(
            hidePrompt = viewState.screenState != ReadyForTap,
            nfcLib = nfcLib,
            mrd5Manager = mrd5Manager,
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
    mrd5Manager: Mrd5Manager,
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
                mrd5Manager,
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

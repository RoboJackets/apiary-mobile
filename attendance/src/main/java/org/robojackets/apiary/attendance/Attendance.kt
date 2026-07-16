package org.robojackets.apiary.attendance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.BluetoothDisabled
import androidx.compose.material.icons.automirrored.outlined.BluetoothSearching
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import com.nxp.nfclib.NxpNfcLib
import org.robojackets.apiary.attendance.model.AttendanceScreenState.Loading
import org.robojackets.apiary.attendance.model.AttendanceScreenState.ReadyForTap
import org.robojackets.apiary.attendance.model.AttendanceState
import org.robojackets.apiary.attendance.model.AttendanceViewModel
import org.robojackets.apiary.base.model.AttendableType
import org.robojackets.apiary.base.ui.ActionPrompt
import org.robojackets.apiary.base.ui.IconWithText
import org.robojackets.apiary.base.ui.icons.PendingIcon
import org.robojackets.apiary.base.ui.icons.WarningIcon
import org.robojackets.apiary.base.ui.mrd5.Mrd5State
import org.robojackets.apiary.base.ui.nfc.BuzzCardPrompt
import org.robojackets.apiary.base.ui.nfc.BuzzCardPromptExternalError
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.base.ui.theme.danger
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.base.ui.util.LoadingSpinner

private fun getExternalError(error: String?): BuzzCardPromptExternalError? {
    error?.let { return BuzzCardPromptExternalError("Unable to save data", it) }
    return null
}

@Suppress("LongMethod", "MagicNumber")
@Composable
private fun Attendance(
    viewState: AttendanceState,
    mrd5State: Mrd5State,
    nfcLib: NxpNfcLib,
    onBuzzcardTap: (buzzcardTap: BuzzCardTap) -> Unit,
    onNavigateToAttendableSelection: () -> Unit,
    onNavigateToMrd5Setup: () -> Unit,
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

            // ── Action buttons ─────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(onClick = onNavigateToAttendableSelection) {
                    Text("Change team or event")
                }
                OutlinedButton(onClick = onNavigateToMrd5Setup) {
                    Icon(
                        imageVector = Icons.Outlined.Bluetooth,
                        contentDescription = "Card reader",
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Reader")
                }
            }

            // ── MRD5 reader status chip ────────────────────────────────────────
            Mrd5StatusChip(
                state = mrd5State,
                onClick = onNavigateToMrd5Setup,
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(top = 6.dp),
            )

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

// ── Reader status chip ─────────────────────────────────────────────────────────

@Composable
private fun Mrd5StatusChip(
    state: Mrd5State,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (icon, label, color) = when (state) {
        is Mrd5State.Ready ->
            Triple(Icons.Outlined.Bluetooth, "Card reader ready", MaterialTheme.colorScheme.primary)
        is Mrd5State.Connected, is Mrd5State.Connecting,
        is Mrd5State.TryingStoredDevice, is Mrd5State.Scanning, is Mrd5State.PickDevice ->
            Triple(Icons.AutoMirrored.Outlined.BluetoothSearching, "Reader connecting…", MaterialTheme.colorScheme.secondary)
        is Mrd5State.Error ->
            Triple(Icons.Outlined.BluetoothDisabled, "Reader error — tap to fix", MaterialTheme.colorScheme.error)
        is Mrd5State.Idle ->
            Triple(Icons.Outlined.BluetoothDisabled, "No card reader — tap to set up", MaterialTheme.colorScheme.outline)
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.12f),
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = label,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

// ── Screen ─────────────────────────────────────────────────────────────────────

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
    val mrd5State by viewModel.mrd5State.collectAsState()

    ContentPadding {
        if (state.selectedAttendable == null && state.error != null) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = CenterHorizontally,
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
                viewState = state,
                mrd5State = mrd5State,
                nfcLib = nfcLib,
                onBuzzcardTap = { viewModel.recordScan(it) },
                onNavigateToAttendableSelection = { viewModel.navigateToAttendableSelection() },
                onNavigateToMrd5Setup = { viewModel.navigateToMrd5Setup() },
            )
        }
    }
}

package org.robojackets.apiary.base.ui.bluetooth

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.robojackets.apiary.base.ui.icons.BluetoothConnectedIcon
import org.robojackets.apiary.base.ui.icons.BluetoothIcon

@Composable
fun Mrd5DetectedChip(
    onNavigateToBuzzCardReaderSettings: () -> Unit
) {
    AssistChip(
        onClick = { onNavigateToBuzzCardReaderSettings() },
        label = { Text("Bluetooth reader available")},
        leadingIcon = { BluetoothIcon() },
    )
}

@Composable
fun Mrd5ConnectingChip() {
    AssistChip(
        onClick = {},
        label = { Text("Connecting to reader...")},
        leadingIcon = { BluetoothIcon() },
    )
}

@Composable
fun Mrd5ConnectedChip() {
    AssistChip(
        onClick = {},
        label = { Text("Reader connected")},
        leadingIcon = { BluetoothConnectedIcon() },
    )
}

@Preview(showBackground = true)
@Composable
fun Mrd5ConnectedChipPreview() {
    Mrd5ConnectedChip()
}

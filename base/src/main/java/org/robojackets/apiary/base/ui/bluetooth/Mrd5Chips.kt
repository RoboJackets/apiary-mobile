package org.robojackets.apiary.base.ui.bluetooth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.base.ui.icons.BluetoothConnectedIcon
import org.robojackets.apiary.base.ui.icons.BluetoothIcon
import org.robojackets.apiary.base.ui.icons.DynamicBatteryIcon

@Composable
fun Mrd5ConnectingChip() {
    AssistChip(
        onClick = {},
        label = { Text("Connecting to reader...") },
        leadingIcon = { BluetoothIcon() },
    )
}

@Composable
fun Mrd5ConnectedChip(
    batteryLevel: Int? = null,
    isBatteryCharging: Boolean? = false,
    onNavigateToBuzzCardReaderSettings: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        AssistChip(
            onClick = { onNavigateToBuzzCardReaderSettings() },
            label = { Text("Reader connected") },
            leadingIcon = { BluetoothConnectedIcon() },
        )
        if (batteryLevel != null) {
            AssistChip(
                modifier = Modifier.padding(start = 8.dp),
                onClick = { onNavigateToBuzzCardReaderSettings() },
                label = { Text("$batteryLevel%") },
                leadingIcon = { DynamicBatteryIcon(batteryLevel = batteryLevel, isCharging = isBatteryCharging == true) },
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun Mrd5ConnectedChipPreview() {
    Mrd5ConnectedChip(
        batteryLevel = 50,
        isBatteryCharging = false,
        onNavigateToBuzzCardReaderSettings = {},
    )
}

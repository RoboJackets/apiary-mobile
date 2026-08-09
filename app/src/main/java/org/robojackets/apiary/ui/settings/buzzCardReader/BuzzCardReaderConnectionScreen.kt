package org.robojackets.apiary.ui.settings.buzzCardReader

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.robojackets.apiary.base.ui.bluetooth.ConnectionState
import org.robojackets.apiary.base.ui.permissions.BluetoothPermissionsRequired
import org.robojackets.apiary.base.ui.util.ContentPadding

@SuppressLint("MissingPermission")
@Composable
fun BuzzCardReaderConnectionScreen(
    viewModel: BuzzCardReaderViewModel,
) {
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val state by viewModel.connection.collectAsStateWithLifecycle()
    val batteryLevel by viewModel.batteryLevel.collectAsStateWithLifecycle()
    val buzzCardTaps by viewModel.buzzCardTaps.collectAsStateWithLifecycle()
    val serialNumber by viewModel.mrd5Manager.deviceSerialNumber.collectAsStateWithLifecycle()
    val firmwareVersion by viewModel.mrd5Manager.deviceFirmwareVersion.collectAsStateWithLifecycle()
    val hardwareVersion by viewModel.mrd5Manager.deviceHardwareVersion.collectAsStateWithLifecycle()
    val softwareVersion by viewModel.mrd5Manager.deviceSoftwareVersion.collectAsStateWithLifecycle()
    val connectedDevice by viewModel.mrd5Manager.connectedDevice.collectAsStateWithLifecycle(initialValue = null)

    ContentPadding {
        BluetoothPermissionsRequired {
            Column {
                if (state == ConnectionState.Disconnected) {
                    Button(onClick = {
                        viewModel.startScan()
                    }) { Text("Start scan") }
                    Text("Found devices:")
                    LazyColumn {
                        items(devices) { device ->
                            Row(Modifier.clickable { viewModel.connect(device.address) }) {
                                Text(device.name ?: "Unknown")
                            }
                        }
                    }
                }
                Text("Connection: $state")
                if (state == ConnectionState.Connected) {
                    Text("Battery level: ${batteryLevel ?: "Unknown"}%")
                    Text("Last BuzzCard tap: $buzzCardTaps")
                    Text("Serial number: $serialNumber")
                    Text("Firmware version: $firmwareVersion")
                    Text("Hardware version: $hardwareVersion")
                    Text("Software version: $softwareVersion")
                    Text("Connected device: $connectedDevice")
                }
            }
        }
    }
}
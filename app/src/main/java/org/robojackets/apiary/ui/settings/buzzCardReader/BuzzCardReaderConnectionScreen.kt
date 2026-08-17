package org.robojackets.apiary.ui.settings.buzzCardReader

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.robojackets.apiary.base.ui.bluetooth.ConnectionState
import org.robojackets.apiary.base.ui.bluetooth.ScanningState
import org.robojackets.apiary.base.ui.error.ErrorMessageWithRetry
import org.robojackets.apiary.base.ui.form.ItemList
import org.robojackets.apiary.base.ui.permissions.BluetoothPermissionsRequired
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.base.ui.util.LoadingSpinner

@Composable
fun ConnectingToReader() {
    Text(text = "Connecting to reader", style = MaterialTheme.typography.headlineSmall)
}

@SuppressLint("MissingPermission")
@Composable
fun BuzzCardReaderConnectionScreen(
    viewModel: BuzzCardReaderViewModel,
) {
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val state by viewModel.connection.collectAsStateWithLifecycle()
    val scanState by viewModel.mrd5Manager.scanState.collectAsStateWithLifecycle()
    val batteryLevel by viewModel.batteryLevel.collectAsStateWithLifecycle()
    val buzzCardTaps by viewModel.buzzCardTaps.collectAsStateWithLifecycle()
    val serialNumber by viewModel.mrd5Manager.deviceSerialNumber.collectAsStateWithLifecycle()
    val firmwareVersion by viewModel.mrd5Manager.deviceFirmwareVersion.collectAsStateWithLifecycle()
    val hardwareVersion by viewModel.mrd5Manager.deviceHardwareVersion.collectAsStateWithLifecycle()
    val softwareVersion by viewModel.mrd5Manager.deviceSoftwareVersion.collectAsStateWithLifecycle()
    val connectedDevice by viewModel.mrd5Manager.connectedDevice.collectAsStateWithLifecycle(initialValue = null)
    val mrd5Error by viewModel.mrd5Manager.error.collectAsStateWithLifecycle()

    ContentPadding {
        BluetoothPermissionsRequired {
            Column {
                if (mrd5Error != null) {
                    ErrorMessageWithRetry(
                        title = mrd5Error,
                        onRetry = { viewModel.resetMrd5Error() },
                        prioritizeRetryButton = true
                    )
                    return@BluetoothPermissionsRequired
                }

                when (state) {
                    ConnectionState.Disconnected -> {
                        when (scanState) {
                            ScanningState.Idle -> {
                                Button(onClick = { viewModel.startScan() }) { Text("Start scan") }
                            }
                            ScanningState.Active -> {
                                ItemList(
                                    items = devices,
                                    onItemSelected = { viewModel.connect(it) },
                                    empty = { LoadingSpinner() },
                                    title = { Text(text = "Found devices", style = MaterialTheme.typography.headlineSmall) },
                                    postItem = { HorizontalDivider() },
                                    itemKey = {
                                        // Prevent IndexOutOfBoundsException when devices is reset to an empty list
                                        if (it <= devices.size - 1) {
                                            return@ItemList devices[it].address
                                        }

                                        return@ItemList it
                                    }
                                ) {
                                    Text(it.name ?: (it.address))
                                }
                            }
                        }
                    }
                    ConnectionState.Connecting -> {
                        ConnectingToReader()
                        LoadingSpinner()
                    }
                    ConnectionState.Initializing -> {
                        ConnectingToReader()
                        LoadingSpinner()
                    }
                    ConnectionState.Connected -> {
                        Text("Connection: $state")
                        Text("Battery level: ${batteryLevel ?: "Unknown"}%")
                        Text("Last BuzzCard tap: $buzzCardTaps")
                        Text("Serial number: $serialNumber")
                        Text("Firmware version: $firmwareVersion")
                        Text("Hardware version: $hardwareVersion")
                        Text("Software version: $softwareVersion")
                        Text("Connected device: $connectedDevice")
                        Button(onClick = { viewModel.mrd5Manager.getVersion() }) {
                            Text("Get version")
                        }
                    }
                }
            }
        }
    }
}
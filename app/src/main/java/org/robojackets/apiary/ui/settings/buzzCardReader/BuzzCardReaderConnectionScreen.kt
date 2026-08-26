package org.robojackets.apiary.ui.settings.buzzCardReader

import android.annotation.SuppressLint
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.robojackets.apiary.BuildConfig
import org.robojackets.apiary.base.ui.bluetooth.ConnectionState
import org.robojackets.apiary.base.ui.bluetooth.Mrd5Command
import org.robojackets.apiary.base.ui.bluetooth.Mrd5Tone
import org.robojackets.apiary.base.ui.bluetooth.ScanningState
import org.robojackets.apiary.base.ui.error.ErrorMessageWithRetry
import org.robojackets.apiary.base.ui.form.ItemList
import org.robojackets.apiary.base.ui.icons.Mrd5Icon
import org.robojackets.apiary.base.ui.permissions.BluetoothAvailableGate
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.base.ui.util.LoadingSpinner
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun ConnectingToReader() {
    Text(text = "Connecting to reader", style = MaterialTheme.typography.headlineSmall)
}

@SuppressLint("MissingPermission", "LongMethod")
@Composable
fun BuzzCardReaderConnectionScreen(
    viewModel: BuzzCardReaderViewModel,
) {
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val state by viewModel.connection.collectAsStateWithLifecycle()
    val scanState by viewModel.mrd5Manager.scanState.collectAsStateWithLifecycle()
    val deviceName by viewModel.mrd5Manager.deviceName.collectAsStateWithLifecycle()
    val batteryLevel by viewModel.batteryLevel.collectAsStateWithLifecycle()
    val buzzCardTaps by viewModel.buzzCardTaps.collectAsStateWithLifecycle()
    val serialNumber by viewModel.mrd5Manager.deviceSerialNumber.collectAsStateWithLifecycle()
    val firmwareVersion by viewModel.mrd5Manager.deviceFirmwareVersion.collectAsStateWithLifecycle()
    val hardwareVersion by viewModel.mrd5Manager.deviceHardwareVersion.collectAsStateWithLifecycle()
    val softwareVersion by viewModel.mrd5Manager.deviceSoftwareVersion.collectAsStateWithLifecycle()
    val bootloaderVersion by viewModel.mrd5Manager.bootloaderVersion.collectAsStateWithLifecycle()
    val applicationVersion by viewModel.mrd5Manager.applicationVersion.collectAsStateWithLifecycle()
    val mrd5Error by viewModel.mrd5Manager.error.collectAsStateWithLifecycle()
    var showAdvancedControls by remember { mutableStateOf(BuildConfig.DEBUG) }

    ContentPadding {
        BluetoothAvailableGate {
            Column {
                if (mrd5Error != null) {
                    ErrorMessageWithRetry(
                        title = mrd5Error,
                        message = "If you have paired with a reader before, go to your Bluetooth settings and forget all devices named MRD5-XXX",
                        onRetry = { viewModel.resetMrd5Error() },
                        prioritizeRetryButton = true
                    )
                    return@BluetoothAvailableGate
                }

                when (state) {
                    ConnectionState.Disconnected -> {
                        when (scanState) {
                            ScanningState.Idle -> {
                                Button(onClick = { viewModel.startScan() }) { Text("Start scan") }
                                Mrd5Icon(modifier = Modifier.size(342.dp))
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
                    ConnectionState.Initializing, ConnectionState.WaitingForPairing -> {
                        ConnectingToReader()
                        LoadingSpinner { Text("Press Pair & Connect in the system notification if prompted", textAlign = TextAlign.Center) }
                    }
                    ConnectionState.Connected -> {
                        Text(text = "Reader connected", style = MaterialTheme.typography.headlineSmall)
                        Text("Device: $deviceName ($serialNumber)")
                        Text("Battery level: ${batteryLevel ?: "Unknown"}%")

                        Button(
                            onClick = { viewModel.disconnect() },
                        ) {
                            Text("Disconnect")
                        }

                        if (!showAdvancedControls)
                        Button(
                            onClick = { showAdvancedControls = true },
                        ) {
                            Text("Show advanced controls")
                        }

                        if (showAdvancedControls) {
                            HorizontalDivider()
                            Text("Connection status: $state")
                            Text("Last BuzzCard tap: $buzzCardTaps")
                            HorizontalDivider()
                            Text("Firmware version: $firmwareVersion")
                            Text("Hardware version: $hardwareVersion")
                            Text("Software version: $softwareVersion")
                            Text("Bootloader version: $bootloaderVersion")
                            Text("Application version: $applicationVersion")
                            HorizontalDivider()

                            Button(onClick = { viewModel.mrd5Manager.sendCommands(listOf(Mrd5Command.Version)) }) {
                                Text("Get version")
                            }

                            HorizontalDivider()

                            Row(
                                Modifier.horizontalScroll(rememberScrollState())
                            ) {
                                for (type in Mrd5Tone.entries) {
                                    Button(onClick = {
                                        viewModel.mrd5Manager.sendCommands(listOf(Mrd5Command.Tone(type)))
                                    }, modifier = Modifier.padding(end = 4.dp)) {
                                        Text("Beep ${type.name}")
                                    }
                                }
                            }
                            var ledColor by remember { mutableStateOf("") }
                            TextField(
                                value = ledColor,
                                onValueChange = { ledColor = it },
                                label = { Text("LED color") }
                            )
                            var ledDuration by remember { mutableStateOf("") }
                            TextField(
                                value = ledDuration,
                                onValueChange = { ledDuration = it },
                                label = { Text("LED duration") }
                            )
                            Button(onClick = {
                                viewModel.mrd5Manager.sendCommands(
                                    listOf(
                                        Mrd5Command.LED(ledColor, ledDuration.toInt().toDuration(DurationUnit.MILLISECONDS))
                                    )
                                )
                            }) {
                                Text("Set LED")
                            }
                        }
                    }
                }
            }
        }
    }
}

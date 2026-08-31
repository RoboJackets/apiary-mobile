package org.robojackets.apiary.ui.settings.buzzCardReader

import android.annotation.SuppressLint
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import org.robojackets.apiary.base.ui.icons.InfoIcon
import org.robojackets.apiary.base.ui.permissions.BluetoothAvailableGate
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.base.ui.util.LoadingSpinner
import timber.log.Timber
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun ConnectingToReader() {
    Text(text = "Connecting to reader", style = MaterialTheme.typography.headlineSmall)
}

@Composable
fun ConnectReaderInfo(modifier: Modifier = Modifier, showIcon: Boolean = true) {
    ListItem(modifier) {
        Row {
            InfoIcon(modifier = Modifier.padding(end = 8.dp).alpha(if (showIcon) 1f else 0f))
            Text("MyRJ can read plastic and digital BuzzCards using an external reader")
        }
    }
}

@Composable
fun TurnOnReaderInstructions(modifier: Modifier = Modifier, showIcon: Boolean = true) {
    ListItem(modifier) {
        Row {
            InfoIcon(modifier = Modifier.padding(end = 8.dp).alpha(if (showIcon) 1f else 0f))
            Text("Turn on the reader by pressing and holding the power button until the light is green and a sound plays")
        }
    }
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
                                Text(text = "Connect BuzzCard reader", style = MaterialTheme.typography.headlineSmall)
                                ConnectReaderInfo(modifier = Modifier.padding(top = 8.dp))
                                TurnOnReaderInstructions(modifier = Modifier.padding(top = 8.dp), showIcon = false)
                                Button(
                                    onClick = { viewModel.startScan() },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) { Text("Search for readers") }
                            }
                            ScanningState.Active -> {
                                Text(text = "Searching for readers", style = MaterialTheme.typography.headlineSmall)
                                TurnOnReaderInstructions(modifier = Modifier.padding(top = 8.dp))
                                ItemList(
                                    items = devices,
                                    onItemSelected = { viewModel.connect(it) },
                                    empty = { LoadingSpinner() },
                                    title = { },
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
                    ConnectionState.Connecting, ConnectionState.Initializing, ConnectionState.WaitingForPairing -> {
                        ConnectingToReader()
                        LoadingSpinner {
                            Text("Press Pair & Connect in the system notification if prompted", textAlign = TextAlign.Center)
                            Button(
                                onClick = { viewModel.disconnect(isUserDisconnect = true) },
                                modifier = Modifier.padding(top = 8.dp),
                            ) {
                                Text("Cancel")
                            }
                        }

                    }
                    ConnectionState.Connected -> {
                        Text(text = "Reader connected", style = MaterialTheme.typography.headlineSmall)
                        Text("Device: $deviceName ($serialNumber)")
                        Text("Battery level: ${batteryLevel ?: "Unknown"}%")

                        Row {
                            Button(
                                onClick = { viewModel.disconnect(isUserDisconnect = true) },
                                modifier = Modifier.padding(end = 4.dp),
                            ) {
                                Text("Disconnect")
                            }

                            Button(
                                onClick = { viewModel.findReader() },
                            ) {
                                Text("Find reader")
                            }
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
                            Text("Last BuzzCard tap: ${buzzCardTaps ?: "None"}")
                            HorizontalDivider()
                            Text("Firmware version: $firmwareVersion")
                            Text("Hardware version: $hardwareVersion")
                            Text("Software version: $softwareVersion")
                            Text("Bootloader version: $bootloaderVersion")
                            Text("Application version: $applicationVersion")
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
                                try {
                                    viewModel.mrd5Manager.sendCommands(
                                        listOf(
                                            Mrd5Command.LED(ledColor,
                                                ledDuration.toInt()
                                                    .toDuration(DurationUnit.MILLISECONDS)
                                            )
                                        )
                                    )
                                } catch (e: NumberFormatException) {
                                    Timber.d(e, "Invalid LED duration: $ledDuration")
                                }
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

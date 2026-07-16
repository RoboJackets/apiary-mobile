package org.robojackets.apiary.base.ui.mrd5

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("MissingPermission")
@Composable
fun Mrd5Screen(manager: Mrd5Manager) {
    val state by manager.state.collectAsState()
    val log by manager.log.collectAsState()
    val lastCardRead by manager.lastCardRead.collectAsState()
    val discoveredDevices by manager.discoveredDevices.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.all { it }) manager.startConnection()
    }

    fun requestPermissionsAndConnect() {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        permissionLauncher.launch(perms)
    }

    // Auto-start connection attempt when the screen opens
    LaunchedEffect(Unit) {
        if (state is Mrd5State.Idle || state is Mrd5State.Error) {
            requestPermissionsAndConnect()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "MRD5 Card Reader",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        StatusBadge(state)

        // ── Device picker ──────────────────────────────────────────────────────
        if (state is Mrd5State.Scanning || state is Mrd5State.PickDevice) {
            DevicePickerSection(
                state = state,
                devices = discoveredDevices,
                onRescan = { manager.startScan() },
                onDevicePicked = { manager.connectTo(it) },
            )
        } else {
            // ── Action buttons (connected / connecting / idle states) ───────────
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state is Mrd5State.Idle || state is Mrd5State.Error) {
                    Button(onClick = { requestPermissionsAndConnect() }) {
                        Text("Connect to reader")
                    }
                }
                if (state is Mrd5State.Ready || state is Mrd5State.Connected ||
                    state is Mrd5State.Connecting || state is Mrd5State.TryingStoredDevice) {
                    OutlinedButton(
                        onClick = { manager.startScan() },
                    ) {
                        Text("Change reader")
                    }
                }
                if (state !is Mrd5State.Idle) {
                    OutlinedButton(
                        onClick = { manager.forgetAndDisconnect() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                    ) {
                        Text("Forget & disconnect")
                    }
                }
            }

            HorizontalDivider()

            // Last card read
            if (lastCardRead != null) {
                val displayValue = when (val cr = lastCardRead) {
                    is CardRead.Gtid -> cr.gtid.toString()
                    is CardRead.AccessCardNumber -> cr.number
                    null -> ""
                }
                val label = when (lastCardRead) {
                    is CardRead.Gtid -> "GTID"
                    is CardRead.AccessCardNumber -> "Access card number"
                    null -> ""
                }
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = displayValue,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            } else if (state is Mrd5State.Ready) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Text(
                        text = "Ready — present a card to the reader",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            HorizontalDivider()

            Text(
                text = "Event log",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    items(log) { entry ->
                        Text(
                            text = entry,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (entry.startsWith("Card read:"))
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

// ── Device picker section ──────────────────────────────────────────────────────

@SuppressLint("MissingPermission")
@Composable
private fun DevicePickerSection(
    state: Mrd5State,
    devices: List<android.bluetooth.BluetoothDevice>,
    onRescan: () -> Unit,
    onDevicePicked: (android.bluetooth.BluetoothDevice) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Device list
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = when {
                            state is Mrd5State.Scanning -> "Searching for readers…"
                            devices.isEmpty() -> "No readers found"
                            else -> "Select a reader"
                        },
                        style = MaterialTheme.typography.titleSmall,
                    )
                    if (state is Mrd5State.Scanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(start = 8.dp),
                            strokeWidth = 2.dp,
                        )
                    }
                }

                if (devices.isNotEmpty()) {
                    HorizontalDivider()
                    devices.forEach { device ->
                        ListItem(
                            headlineContent = {
                                Text(device.name ?: device.address)
                            },
                            supportingContent = {
                                Text(
                                    text = device.address,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            },
                            leadingContent = {
                                Icon(Icons.Outlined.Bluetooth, contentDescription = null)
                            },
                            trailingContent = {
                                Icon(Icons.Outlined.ChevronRight, contentDescription = "Connect")
                            },
                            modifier = Modifier.clickable { onDevicePicked(device) },
                        )
                        HorizontalDivider()
                    }
                }

                if (state is Mrd5State.PickDevice) {
                    OutlinedButton(
                        onClick = onRescan,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Text("Scan again")
                    }
                }
            }
        }

        // Pairing help
        PairingHelp()
    }
}

@Composable
private fun PairingHelp() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Pairing help",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.SemiBold,
            )
            PairingStep(
                number = "1",
                text = "Make sure the MRD5 is on. Press and hold the power button for " +
                    "three seconds until the power LED turns green and you hear a " +
                    "five-tone ascending chime.",
            )
            PairingStep(
                number = "2",
                text = "If the reader doesn't appear in the list above, it may be paired " +
                    "to another device. While the reader is on, press the power button " +
                    "twice within two seconds to clear existing pairings — the status " +
                    "LED will flash yellow on each press, and after the second press " +
                    "you'll hear a five-tone ascending chime confirming the pairings " +
                    "have been cleared. Then tap Scan again.",
            )
            PairingStep(
                number = "3",
                text = "A successful pairing is confirmed by a blue LED and a five-tone " +
                    "ascending chime on the reader.",
            )
        }
    }
}

@Composable
private fun PairingStep(number: String, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.secondary,
        ) {
            Text(
                text = number,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

// ── Status badge ───────────────────────────────────────────────────────────────

@Composable
private fun StatusBadge(state: Mrd5State) {
    val (label, color) = when (state) {
        is Mrd5State.Idle -> "Not connected" to MaterialTheme.colorScheme.outline
        is Mrd5State.TryingStoredDevice -> "Connecting to ${state.name}…" to MaterialTheme.colorScheme.tertiary
        is Mrd5State.Scanning -> "Scanning…" to MaterialTheme.colorScheme.tertiary
        is Mrd5State.PickDevice -> "Select a reader below" to MaterialTheme.colorScheme.tertiary
        is Mrd5State.Connecting -> "Connecting…" to MaterialTheme.colorScheme.secondary
        is Mrd5State.Connected -> "Connected" to MaterialTheme.colorScheme.secondary
        is Mrd5State.Ready -> "Ready — present card" to MaterialTheme.colorScheme.primary
        is Mrd5State.Error -> "Error: ${state.message}" to MaterialTheme.colorScheme.error
    }
    Surface(shape = MaterialTheme.shapes.small, color = color.copy(alpha = 0.15f)) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = color,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
        )
    }
}

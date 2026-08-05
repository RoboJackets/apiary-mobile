package org.robojackets.apiary.ui.settings.buzzCardReader

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
import org.robojackets.apiary.base.ui.permissions.BluetoothPermissionsRequired
import org.robojackets.apiary.base.ui.util.ContentPadding

private const val PERMISSION_REQUEST_CODE = 1

@Composable
fun BuzzCardReaderConnectionScreen(
    viewModel: BuzzCardReaderViewModel,
) {
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val state by viewModel.connection.collectAsStateWithLifecycle()
    ContentPadding {
        BluetoothPermissionsRequired {
            Column {
                Button(onClick = {
                    viewModel.startScan()
                }) { Text("Start scan") }
                Text("Connection: $state")
                LazyColumn {
                    items(devices) { device ->
                        Row(Modifier.clickable { viewModel.connect(device.address) }) {
                            Text(device.name ?: "Unknown")
                        }
                    }
                }
            }
        }
    }
}
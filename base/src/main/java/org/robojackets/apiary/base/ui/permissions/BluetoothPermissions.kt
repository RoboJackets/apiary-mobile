package org.robojackets.apiary.base.ui.permissions

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.robojackets.apiary.base.ui.ActionPrompt
import org.robojackets.apiary.base.ui.bluetooth.rememberBluetoothEnabled
import org.robojackets.apiary.base.ui.error.ErrorMessageWithRetry
import org.robojackets.apiary.base.ui.icons.BluetoothDisabledIcon
import org.robojackets.apiary.base.ui.icons.ErrorIcon
import org.robojackets.apiary.base.ui.theme.danger
import timber.log.Timber

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothAvailableGate(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val bluetoothPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    )
    val isBluetoothEnabled by rememberBluetoothEnabled()
    var error by remember { mutableStateOf<String?>(null) }

    when {
        !bluetoothPermissionsState.allPermissionsGranted ||
                bluetoothPermissionsState.revokedPermissions.isNotEmpty() ||
                bluetoothPermissionsState.shouldShowRationale -> {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment =
                Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
            ) {
                ActionPrompt(
                    icon = { ErrorIcon(Modifier.size(114.dp), tint = danger) },
                    title = "Bluetooth access required",
                ) {
                    Button(onClick = { bluetoothPermissionsState.launchMultiplePermissionRequest() }) {
                        Text("Continue")
                    }
                }
                if (bluetoothPermissionsState.shouldShowRationale) {
                    ListItem {
                        Text("Bluetooth is used to connect to external BuzzCard readers")
                    }
                }
                if (bluetoothPermissionsState.revokedPermissions.isNotEmpty()) {
                    ListItem {
                        Text(
                            "If nothing happens when you press Continue, open the Android " +
                                "settings for this app and grant the Nearby Devices permission."
                        )
                    }
                }
            }
        }

        !isBluetoothEnabled -> {
            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                ActionPrompt(
                    icon = { BluetoothDisabledIcon(Modifier.size(114.dp)) },
                    title = "Enable Bluetooth to continue",
                ) {
                    Button(onClick = {
                        try {
                            context.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                        } catch (e: SecurityException) {
                            Timber.e(e)
                            error = "Unable to enable Bluetooth"
                        }
                    }) {
                        Text("Enable Bluetooth")
                    }
                }
            }
        }

        error != null -> {
            ErrorMessageWithRetry(
                title = error,
                onRetry = { error = null },
                prioritizeRetryButton = false
            )
        }

        else -> {
            content()
        }
    }
}

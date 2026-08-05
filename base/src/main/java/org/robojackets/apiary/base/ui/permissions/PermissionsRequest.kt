package org.robojackets.apiary.base.ui.permissions

import android.Manifest
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


// FIXME: Make generic?
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothPermissionsRequired(content: @Composable () -> Unit) {
    val bluetoothPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    )

    when {
        !bluetoothPermissionsState.allPermissionsGranted -> {
            Button(onClick = { bluetoothPermissionsState.launchMultiplePermissionRequest() }) {
                Text("Request Bluetooth permissions")
            }
        }
        bluetoothPermissionsState.revokedPermissions.isNotEmpty() || bluetoothPermissionsState.shouldShowRationale -> {
            Text("Bluetooth permissions need to be granted in Android settings for this app")
        }
        else -> {
            content()
        }
    }
}
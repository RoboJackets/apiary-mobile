package org.robojackets.apiary.base.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberBluetoothEnabled(): State<Boolean> {
    val context = LocalContext.current

    val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    val isBluetoothEnabled = remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) }

    DisposableEffect(context, bluetoothAdapter) {
        if (bluetoothAdapter == null) {
            return@DisposableEffect onDispose {}
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR,
                    )

                    isBluetoothEnabled.value = (state == BluetoothAdapter.STATE_ON)
                }
            }
        }

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(receiver, filter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    return isBluetoothEnabled
}

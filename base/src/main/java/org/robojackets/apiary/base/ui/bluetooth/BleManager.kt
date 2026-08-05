package org.robojackets.apiary.base.ui.bluetooth

import android.Manifest
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.MATCH_MODE_STICKY
import android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY
import android.content.Context
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

enum class ConnectionState {
    Disconnected,
    Connecting,
    Connected,
}

// Based on https://medium.com/@YodgorbekKomilo/designing-a-robust-ble-system-in-android-with-jetpack-compose-a7941bec8c66
@Singleton
class BleManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val adapter = bluetoothManager.adapter

    private val _scanResults = MutableSharedFlow<ScanResult>()
    val scanResults = _scanResults.asSharedFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    val connectionState = _connectionState.asStateFlow()

    private var gatt: BluetoothGatt? = null

    fun startScan() {
        val scanner = adapter?.bluetoothLeScanner ?: return
        val callback = object : ScanCallback() {
            override fun onScanResult(type: Int, result: ScanResult) {
                CoroutineScope(Dispatchers.IO).launch {
                    Timber.tag("BleManager")
                        .d("Scan result: ${result.device.address}")
                    _scanResults.emit(result)
                }
            }
        }
        scanner
            .startScan(listOf(
                ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID.fromString("00035b03-58e6-07dd-021a-08123a000300"))).build()
            ),
                ScanSettings.Builder()
                    .setScanMode(SCAN_MODE_LOW_LATENCY)
                    .setMatchMode(MATCH_MODE_STICKY).build(),
                callback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect(address: String) {
        val device = adapter?.getRemoteDevice(address) ?: return
        gatt = device.connectGatt(context, false, gattCallback)
        _connectionState.value = ConnectionState.Connecting
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                _connectionState.value = ConnectionState.Connected
                g.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                _connectionState.value = ConnectionState.Disconnected
                g.close()
            }
        }
    }
}
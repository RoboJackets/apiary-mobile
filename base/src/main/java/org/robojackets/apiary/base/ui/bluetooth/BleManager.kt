package org.robojackets.apiary.base.ui.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice.TRANSPORT_LE
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.MATCH_MODE_STICKY
import android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.ArrayDeque
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

enum class ConnectionState {
    Disconnected,
    Connecting,
    Connected,
}

val MLDP_SERVICE_UUID: UUID = UUID.fromString("00035b03-58e6-07dd-021a-08123a000300")
val MLDP_DATA_UUID: UUID = UUID.fromString("00035b03-58e6-07dd-021a-08123a000301")
val CCCD_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

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

    private val opQueue = ArrayDeque<() -> Unit>()
    private var opPending = false

    private var gatt: BluetoothGatt? = null
    private var scanCallback: ScanCallback? = null

    fun startScan() {
        val scanner = adapter?.bluetoothLeScanner ?: return
        scanCallback = object : ScanCallback() {
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
                scanCallback)
    }

    private fun enqueue(op: () -> Unit) {
        opQueue.add(op)
        if (!opPending) dispatchNext()
    }

    private fun dispatchNext() {
        if (opQueue.isEmpty()) { opPending = false; return }
        opPending = true
        opQueue.poll()!!.invoke()
    }
    private fun opComplete() = dispatchNext()


    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect(address: String) {
        val device = adapter?.getRemoteDevice(address) ?: return
        
        gatt?.close()
        gatt = null
        
        Timber.d("Connecting to $address via TRANSPORT_LE")
        gatt = device.connectGatt(context, false, gattCallback, TRANSPORT_LE)
        _connectionState.value = ConnectionState.Connecting
    }

    private fun writeCccd(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, value: ByteArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gatt.writeDescriptor(descriptor, value)
        } else {
            @Suppress("DEPRECATION")
            descriptor.value = value
            @Suppress("DEPRECATION")
            gatt.writeDescriptor(descriptor)
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Timber.d("Connected to GATT server (status: $status)")
                _connectionState.value = ConnectionState.Connected
                
                g.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)
                g.discoverServices()

                val scanner = adapter?.bluetoothLeScanner ?: return
                scanCallback?.let {
                    Timber.d("Stopping scan")
                    scanner.stopScan(it)
                    scanCallback = null
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Timber.d("Disconnected (status: $status)")
                _connectionState.value = ConnectionState.Disconnected

                debounceJob?.cancel()
                opQueue.clear()
                opPending = false
                rxBuffer.clear()

                g.close()

                if (g == gatt) {
                    gatt = null
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Timber.d("Service discovery failed: $status")
                return
            }
            val dataChar = gatt.getService(MLDP_SERVICE_UUID)
                ?.getCharacteristic(MLDP_DATA_UUID)
            if (dataChar == null) {
                Timber.d("MLDP data characteristic not found")
                return
            }
            Timber.d("Services discovered. Writing CCCD immediately (race vs GPIO4)…")
            gatt.setCharacteristicNotification(dataChar, true)
            enqueue {
                val cccd = dataChar.getDescriptor(CCCD_UUID)
                if (cccd == null) {
                    Timber.d("CCCD descriptor not found")
                    opComplete(); return@enqueue
                }
                writeCccd(gatt, cccd, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            }
        }

        @Deprecated("Deprecated for Android 13+")
        @Suppress("DEPRECATION")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            with(characteristic) {
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        Timber.tag("BluetoothGattCallback")
                            .i("Read characteristic $uuid:n${value.toHexString()}")
                    }
                    BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                        Timber.tag("BluetoothGattCallback").e("Read not permitted for $uuid!")
                    }
                    else -> {
                        Timber.tag("BluetoothGattCallback")
                            .e("Characteristic read failed for $uuid, error: $status")
                    }
                }
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int,
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Timber.d("CCCD write confirmed — ready for card reads")
            } else {
                Timber.d("CCCD write failed: status=$status")
            }
            opComplete()
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
        ) {
            Timber.d("Characteristic changed: ${characteristic.uuid} value: ${value.contentToString()}")
            if (characteristic.uuid == MLDP_DATA_UUID) handleRx(value)
        }

        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
        ) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                Timber.d("Characteristic changed: ${characteristic.uuid} value: ${characteristic.value.contentToString()}")
                if (characteristic.uuid == MLDP_DATA_UUID) handleRx(characteristic.value)
            } else {
                Timber.d("2-param onCharactericChanged called but device is below Tiramisu")
            }
        }
    }

    private var cccd: BluetoothGattDescriptor? = null
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun readDataTest() {
        CoroutineScope(Dispatchers.Main).launch {
            val serviceUuid = UUID.fromString("00035b03-58e6-07dd-021a-08123a000300")
            val charUuid = UUID.fromString("00035b03-58e6-07dd-021a-08123a000301")
            val cccdUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

            val currentGatt = gatt
            if (currentGatt == null) {
                Timber.e("readDataTest: gatt is null!")
                return@launch
            }

            val char = currentGatt.getService(serviceUuid)?.getCharacteristic(charUuid)
            cccd = char?.getDescriptor(cccdUuid)

            if (char == null || cccd == null) {
                Timber.e("Characteristic or CCCD not found! (char: ${char != null}, cccd: ${cccd != null})")
                return@launch
            }

            val notifyResult = currentGatt.setCharacteristicNotification(char, true)
            Timber.d("setCharacteristicNotification result: $notifyResult")

            val writeResult = currentGatt.writeDescriptor(
                cccd!!,
                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            )
            Timber.d("writeDescriptor result: $writeResult")
        }
    }

    private val rxBuffer = StringBuilder()
    private var debounceJob: Job? = null
    private val managerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private fun handleRx(bytes: ByteArray) {
        rxBuffer.append(String(bytes, Charsets.US_ASCII))
        Timber.d("MRD5 RX chunk (buffer: '${rxBuffer.toString().trim()}')")
        debounceJob?.cancel()
        debounceJob = managerScope.launch {
            delay(80L.milliseconds)
            val raw = rxBuffer.toString().trim()
            rxBuffer.clear()
            if (raw.isEmpty()) {
                Timber.d("handleRx - raw is empty")
                return@launch
            }

           Timber.d("handleRx - raw is $raw")
        }
    }
}

fun BluetoothGattCharacteristic.isReadable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_READ)

fun BluetoothGattCharacteristic.isWritable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE)

fun BluetoothGattCharacteristic.isWritableWithoutResponse(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)

fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean {
    return properties and property != 0
}

fun BluetoothGattCharacteristic.isIndicatable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_INDICATE)

fun BluetoothGattCharacteristic.isNotifiable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_NOTIFY)

fun ByteArray.toHexString(): String =
    joinToString(separator = " ", prefix = "0x") { String.format("%02X", it) }


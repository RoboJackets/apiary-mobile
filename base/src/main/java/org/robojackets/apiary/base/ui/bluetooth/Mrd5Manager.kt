package org.robojackets.apiary.base.ui.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice.TRANSPORT_LE
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattConnectionSettings
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
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import timber.log.Timber
import java.util.ArrayDeque
import java.util.UUID
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

enum class ConnectionState {
    Disconnected,
    Connecting,
    Initializing,
    Connected,
    Error,
}

enum class ScanningState {
    Unknown,
    Idle,
    Active,
    Error,
}

private val MLDP_SERVICE_UUID: UUID = UUID.fromString("00035b03-58e6-07dd-021a-08123a000300")
private val MLDP_DATA_UUID: UUID = UUID.fromString("00035b03-58e6-07dd-021a-08123a000301")
private val CCCD_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

// Based on https://medium.com/@YodgorbekKomilo/designing-a-robust-ble-system-in-android-with-jetpack-compose-a7941bec8c66
// and https://punchthrough.com/android-ble-guide/
@Singleton
class Mrd5Manager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val scanner by lazy {
        adapter.bluetoothLeScanner
    }
    private val adapter: BluetoothAdapter by lazy {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        bluetoothManager.adapter
    }

    private val _scanResults = MutableSharedFlow<ScanResult>()
    val scanResults = _scanResults.asSharedFlow()

    private val _batteryLevel = MutableStateFlow<Int?>(null)
    val batteryLevel = _batteryLevel.asStateFlow()

    private val _buzzCardTaps = MutableSharedFlow<BuzzCardTap>()
    val buzzCardTaps = _buzzCardTaps.asSharedFlow()

    private val _scanState = MutableStateFlow(ScanningState.Unknown)
    val scanState = _scanState.asStateFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    val connectionState = _connectionState.asStateFlow()

    private val opQueue = ArrayDeque<() -> Unit>()
    private var opPending = false

    private var gatt: BluetoothGatt? = null

    private fun enqueue(op: () -> Unit) {
        opQueue.add(op)
        if (!opPending) dispatchNext()
    }

    private fun dispatchNext() {
        if (opQueue.isEmpty()) {
            opPending = false; return
        }
        opPending = true
        opQueue.poll()?.invoke() ?: {
            Timber.w("opQueue.poll() returned null")
        }
    }

    private fun opComplete() = dispatchNext()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(type: Int, result: ScanResult) {
            CoroutineScope(Dispatchers.IO).launch {
                Timber.d("Scan result: ${result.device.address}")
                _scanResults.emit(result)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Timber.e("Scan failed: $errorCode")
            _scanState.value = ScanningState.Error
        }
    }

    /**
     * Start scanning for Bluetooth devices. Optionally specify a list of filters and settings.
     */
    fun startScanning() {
        if (_scanState.value == ScanningState.Active) {
            throw IllegalStateException("Scan is already active")
        }

        _scanState.value = ScanningState.Active

        scanner?.startScan(
            listOf(
                ScanFilter.Builder()
                    .setServiceUuid(ParcelUuid(UUID.fromString("00035b03-58e6-07dd-021a-08123a000300")))
                    .build()
            ),
            ScanSettings.Builder()
                .setScanMode(SCAN_MODE_LOW_LATENCY)
                .setMatchMode(MATCH_MODE_STICKY).build(), scanCallback
        ) ?: {
            throw IllegalStateException("Bluetooth scanner is null")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScanning() {
        scanner?.stopScan(scanCallback)
        _scanState.value = ScanningState.Idle
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect(address: String) {
        val device = adapter.getRemoteDevice(address)
        
        gatt?.close()
        gatt = null

        Timber.d("Connecting to $device")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN) {
            device.connectGatt(BluetoothGattConnectionSettings.Builder()
                .setTransport(TRANSPORT_LE)
                .setAutoConnectEnabled(false)
                .setAutomaticMtuEnabled(true).build(),
                Executors.newSingleThreadExecutor(),
                gattCallback
            )
        } else {
            @Suppress("DEPRECATION")
            device.connectGatt(context, false, gattCallback, TRANSPORT_LE)
        }
        _connectionState.value = ConnectionState.Connecting
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun writeCccd(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor,
        value: ByteArray
    ) {
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
        @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
        override fun onConnectionStateChange(_gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Timber.d("Connected to GATT server (status: $status)")
                _connectionState.value = ConnectionState.Initializing

                _gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)
                _gatt.discoverServices()
                gatt = _gatt

                stopScanning()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Timber.d("Disconnected (status: $status)")
                _connectionState.value = ConnectionState.Disconnected

                debounceJob?.cancel()
                opQueue.clear()
                opPending = false
                rxBuffer.clear()

                _gatt.close()

                if (_gatt == gatt) {
                    gatt = null
                }
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                _connectionState.value = ConnectionState.Error
                Timber.e("Service discovery failed: $status")
                return
            }

            val dataChar = gatt.getService(MLDP_SERVICE_UUID)
                ?.getCharacteristic(MLDP_DATA_UUID)

            if (dataChar == null) {
                throw IllegalStateException("MLDP data characteristic not found")
            }

            gatt.setCharacteristicNotification(dataChar, true)
            enqueue {
                val cccd = dataChar.getDescriptor(CCCD_UUID)
                if (cccd == null) {
                    Timber.e("CCCD descriptor not found")
                    opComplete(); return@enqueue
                }
                writeCccd(gatt, cccd, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int,
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Timber.d("CCCD write confirmed")
                _connectionState.value = ConnectionState.Connected
            } else {
                Timber.d("CCCD write failed: status=$status")
                _connectionState.value = ConnectionState.Error
            }
            opComplete()
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
        ) {
            Timber.d("Characteristic changed: ${characteristic.uuid} value: ${value.contentToString()}")
            if (characteristic.uuid == MLDP_DATA_UUID) {
                handleRx(value)
            } else {
                Timber.d("Unrecognized characteristic: ${characteristic.uuid} with value ${value.contentToString()}")
            }
        }

        @Deprecated("Deprecated in Android 13+")
        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
        ) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                Timber.d("Characteristic changed: ${characteristic.uuid} value: ${characteristic.value.contentToString()}")
                if (characteristic.uuid == MLDP_DATA_UUID) {
                    handleRx(characteristic.value)
                }
            } else {
                Timber.d("2-param onCharacteristicChanged called but device is at/above Android 13")
            }
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
            val transmission = Mrd5Transmission.fromString(raw)
            when (transmission) {
                is Mrd5Transmission.BuzzCard -> {
                    _buzzCardTaps.emit(transmission.tap)
                }
                is Mrd5Transmission.BatteryLevel -> {
                    _batteryLevel.value = transmission.level
                }
                else -> {
                    Timber.w("handleRx - unrecognized transmission: $transmission")
                }
            }
            Timber.d("handleRx - transmission is $transmission")
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


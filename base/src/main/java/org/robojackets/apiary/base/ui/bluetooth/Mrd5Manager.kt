package org.robojackets.apiary.base.ui.bluetooth

import android.Manifest
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.MATCH_MODE_STICKY
import android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY
import android.content.Context
import androidx.annotation.RequiresPermission
import com.juul.kable.Advertisement
import com.juul.kable.ObsoleteKableApi
import com.juul.kable.Peripheral
import com.juul.kable.PlatformAdvertisement
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.WriteType
import com.juul.kable.characteristicOf
import com.juul.kable.logs.Logging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.robojackets.apiary.base.model.Device
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid

enum class ConnectionState {
    Disconnected,
    Connecting,
    Initializing,
    Connected,
}

enum class ScanningState {
    Idle,
    Active,
}

private val MLDP_SERVICE_UUID: Uuid = Uuid.parse("00035b03-58e6-07dd-021a-08123a000300")
private val MLDP_DATA_UUID: Uuid = Uuid.parse("00035b03-58e6-07dd-021a-08123a000301")
private val MLDP_WRITE_UUID: Uuid = Uuid.parse("00035b03-58e6-07dd-021a-08123a0003ff")
private val DEVICE_INFO_SERVICE_UUID = Uuid.parse("0000180a-0000-1000-8000-00805f9b34fb")
private val MODEL_NUMBER_CHAR_UUID = Uuid.parse("00002a24-0000-1000-8000-00805f9b34fb")
private val SERIAL_NUMBER_CHAR_UUID = Uuid.parse("00002a25-0000-1000-8000-00805f9b34fb")
private val FIRMWARE_REVISION_CHAR_UUID = Uuid.parse("00002a26-0000-1000-8000-00805f9b34fb")
private val HARDWARE_REVISION_CHAR_UUID = Uuid.parse("00002a27-0000-1000-8000-00805f9b34fb")
private val SOFTWARE_REVISION_CHAR_UUID = Uuid.parse("00002a28-0000-1000-8000-00805f9b34fb")
private val MANUFACTURER_CHAR_UUID = Uuid.parse("00002a29-0000-1000-8000-00805f9b34fb")

// Based on https://medium.com/@YodgorbekKomilo/designing-a-robust-ble-system-in-android-with-jetpack-compose-a7941bec8c66
// and https://punchthrough.com/android-ble-guide/
@Singleton
class Mrd5Manager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var scanner : Job? = null

    private val _scanResults = MutableStateFlow<List<PlatformAdvertisement>>(emptyList())
    val scanResults = _scanResults.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _batteryLevel = MutableStateFlow<Int?>(null)
    val batteryLevel = _batteryLevel.asStateFlow()

    private val _deviceModel = MutableStateFlow<String?>(null)
    val deviceModel = _deviceModel.asStateFlow()

    private val _deviceSerialNumber = MutableStateFlow<String?>(null)
    val deviceSerialNumber = _deviceSerialNumber.asStateFlow()

    private val _deviceFirmwareVersion = MutableStateFlow<String?>(null)
    val deviceFirmwareVersion = _deviceFirmwareVersion.asStateFlow()

    private val _deviceHardwareVersion = MutableStateFlow<String?>(null)
    val deviceHardwareVersion = _deviceHardwareVersion.asStateFlow()

    private val _deviceSoftwareVersion = MutableStateFlow<String?>(null)
    val deviceSoftwareVersion = _deviceSoftwareVersion.asStateFlow()

    private val _deviceManufacturer = MutableStateFlow<String?>(null)
    val deviceManufacturer = _deviceManufacturer.asStateFlow()

    private val _connectedDevice = MutableSharedFlow<Device?>()
    val connectedDevice = _connectedDevice.asSharedFlow()

    private val _buzzCardTaps = MutableSharedFlow<BuzzCardTap>()
    val buzzCardTaps = _buzzCardTaps.asSharedFlow()

    private val _scanState = MutableStateFlow(ScanningState.Idle)
    val scanState = _scanState.asStateFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    val connectionState = _connectionState.asStateFlow()

    private lateinit var peripheral: Peripheral
    private var connectionScope : CoroutineScope? = null

    private val deviceInfoUUIDs = listOf(
        MODEL_NUMBER_CHAR_UUID,
        SERIAL_NUMBER_CHAR_UUID,
        FIRMWARE_REVISION_CHAR_UUID,
        HARDWARE_REVISION_CHAR_UUID,
        SOFTWARE_REVISION_CHAR_UUID,
        MANUFACTURER_CHAR_UUID,
    )

    fun resetError() {
        _error.value = null
    }

    @OptIn(ObsoleteKableApi::class)
    fun startScanning(scope: CoroutineScope) {
        _error.value = null
        _scanState.value = ScanningState.Active
        scanner = scope.launch {
            Scanner {
                scanSettings = ScanSettings.Builder()
                    .setScanMode(SCAN_MODE_LOW_LATENCY)
                    .setMatchMode(MATCH_MODE_STICKY).build()
                filters {
                    match {
                        services = listOf(Uuid.parse("00035b03-58e6-07dd-021a-08123a000300"))
                    }
                }
            }.advertisements.collect { advertisement ->
                Timber.d("Scan result: $advertisement")
                _scanResults.update { existing ->
                    (existing + advertisement).distinctBy { it.address }
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScanning() {
        scanner?.cancel("stopScanning called")
        _scanState.value = ScanningState.Idle
        _scanResults.value = emptyList()
    }

    private suspend fun initialize() {
        _connectionState.value = ConnectionState.Initializing

        deviceInfoUUIDs.forEach {
            val value = peripheral.read(characteristic = characteristicOf(
                service = DEVICE_INFO_SERVICE_UUID,
                characteristic = it,
            ))
            when(it) {
                MODEL_NUMBER_CHAR_UUID -> _deviceModel.value = String(value, Charsets.US_ASCII)
                SERIAL_NUMBER_CHAR_UUID -> _deviceSerialNumber.value = String(value, Charsets.US_ASCII)
                FIRMWARE_REVISION_CHAR_UUID -> _deviceFirmwareVersion.value = String(value, Charsets.US_ASCII)
                HARDWARE_REVISION_CHAR_UUID -> _deviceHardwareVersion.value = String(value, Charsets.US_ASCII)
                SOFTWARE_REVISION_CHAR_UUID -> _deviceSoftwareVersion.value = String(value, Charsets.US_ASCII)
                MANUFACTURER_CHAR_UUID -> _deviceManufacturer.value = String(value, Charsets.US_ASCII)
            }
        }

    }

    suspend fun storeDevice() {
        if (_deviceModel.value != null &&
            _deviceSerialNumber.value != null &&
            _deviceFirmwareVersion.value != null &&
            _deviceHardwareVersion.value != null &&
            _deviceSoftwareVersion.value != null &&
            _batteryLevel.value != null &&
            _deviceManufacturer.value != null
        ) {
            _connectedDevice.emit(
                Device(
                    model = _deviceModel.value!!,
                    serialNumber = _deviceSerialNumber.value!!.toInt(),
                    firmwareVersion = _deviceFirmwareVersion.value!!,
                    hardwareVersion = _deviceHardwareVersion.value!!,
                    softwareVersion = _deviceSoftwareVersion.value!!,
                    batteryPercentage = _batteryLevel.value!!,
                    manufacturer = _deviceManufacturer.value!!,
                )
            )
        }
    }

    @OptIn(ObsoleteKableApi::class)
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    suspend fun connect(advertisement: Advertisement?) {
        if (advertisement == null) {
            _error.value = "Reader connection failed"
            Timber.w("peripheral was null, ignoring")
        }

        _connectionState.value = ConnectionState.Connecting
        stopScanning()

        advertisement?.let {
            peripheral = Peripheral(it) {
                logging {
                    level = Logging.Level.Data
                    data = Logging.DataProcessor { bytes, _, _, _, _ ->
                        String(bytes, Charsets.US_ASCII)
                    }
                }
                onServicesDiscovered {
                    initialize()
                }
            }
            connectionScope = peripheral.connect()
            connectionScope?.launch {
                var attempts = 0
                val maxAttempts = 15
                while (_batteryLevel.value == null && ++attempts <= maxAttempts) {
                    Timber.d("Wait for battery level - attempt $attempts/$maxAttempts")
                    delay(1.seconds)
                }
                if (attempts <= maxAttempts) {
                    _connectionState.value = ConnectionState.Connected
                    storeDevice()
                } else {
                    Timber.w("Timed out getting battery status")
                    _error.value = "Reader connection failed"
                    peripheral.disconnect()
                }
            }

            peripheral.scope.launch {
                peripheral.state.collect { peripheralState ->
                    if (peripheralState is State.Disconnected) {
                        Timber.d("Peripheral disconnected: $peripheralState")
                        _connectionState.value = ConnectionState.Disconnected
                    }
                }
            }

            connectionScope?.launch {
                peripheral.observe(
                    characteristic = characteristicOf(
                        service = MLDP_SERVICE_UUID,
                        characteristic = MLDP_DATA_UUID,
                    )
                ).collect { bytes ->
                    Timber.d("RX: ${String(bytes, Charsets.US_ASCII)}")
                    handleRx(bytes)
                }
            }
        }
    }

    fun getVersion() {
        connectionScope!!.launch {
            val ver_response = peripheral.write(characteristicOf(
                service = MLDP_SERVICE_UUID,
                characteristic = MLDP_WRITE_UUID,
            ), data = "VER:\n".toByteArray(), WriteType.WithoutResponse)
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


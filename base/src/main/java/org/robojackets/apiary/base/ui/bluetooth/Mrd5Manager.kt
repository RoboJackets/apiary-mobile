package org.robojackets.apiary.base.ui.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.MATCH_MODE_STICKY
import android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY
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
import org.robojackets.apiary.base.GlobalSettings
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
    WaitingForPairing,
    Connected,
}

enum class ScanningState {
    Idle,
    Active,
}

private val MLDP_SERVICE_UUID: Uuid = Uuid.parse("00035b03-58e6-07dd-021a-08123a000300")
private val MLDP_DATA_UUID: Uuid = Uuid.parse("00035b03-58e6-07dd-021a-08123a000301")
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
    val globalSettings: GlobalSettings,
) {
    init {
        Timber.d("Mrd5Manager was initialized!")
    }

    private var scanner: Job? = null

    private val _scanResults = MutableStateFlow<List<PlatformAdvertisement>>(emptyList())
    val scanResults = _scanResults.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _batteryLevel = MutableStateFlow<Int?>(null)
    val batteryLevel = _batteryLevel.asStateFlow()

    private val _deviceName = MutableStateFlow<String?>(null)
    val deviceName = _deviceName.asStateFlow()

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

    private val _bootloaderVersion = MutableStateFlow<String?>(null)
    val bootloaderVersion = _bootloaderVersion.asStateFlow()

    private val _applicationVersion = MutableStateFlow<String?>(null)
    val applicationVersion = _applicationVersion.asStateFlow()

    private val _buzzCardTaps = MutableSharedFlow<BuzzCardTap>()
    val buzzCardTaps = _buzzCardTaps.asSharedFlow()

    private val _scanState = MutableStateFlow(ScanningState.Idle)
    val scanState = _scanState.asStateFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    val connectionState = _connectionState.asStateFlow()

    private lateinit var peripheral: Peripheral
    private var connectionScope: CoroutineScope? = null
    private val managerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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

//    private val reattemptConnectionMutex = Mutex()
//
//    @OptIn(FlowPreview::class)
//    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
//    fun attemptConnectionToSavedDevice() {
//        managerScope.launch {
//            reattemptConnectionMutex.withLock {
//                if (globalSettings.mrd5DeviceMac == null) {
//                    Timber.d("No saved device to connect to")
//                    return@launch
//                }
//
//                if (_connectionState.value != ConnectionState.Disconnected) {
//                    Timber.d("Connection state is not disconnected, ignoring")
//                    return@launch
//                }
//                stopScanning()
//                startScanning(managerScope)
//                scanResults.collect { result ->
//                    result.find { it.address == globalSettings.mrd5DeviceMac }?.let {
//                        Timber.d("Saved device MAC located")
//                        connect(it)
//                    }
//                }
//            }
//        }
//    }

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
                        services = listOf(MLDP_SERVICE_UUID)
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
        Timber.d("Stop scanning called")
        scanner?.cancel("stopScanning called")
        _scanState.value = ScanningState.Idle
        _scanResults.value = emptyList()
    }

    private suspend fun initialize() {
        try {
            _connectionState.value = ConnectionState.Initializing
            deviceInfoUUIDs.forEach {
                val value = peripheral.read(
                    characteristic = characteristicOf(
                        service = DEVICE_INFO_SERVICE_UUID,
                        characteristic = it,
                    )
                )
                when (it) {
                    MODEL_NUMBER_CHAR_UUID -> _deviceModel.value =
                        String(value, Charsets.US_ASCII)

                    SERIAL_NUMBER_CHAR_UUID -> _deviceSerialNumber.value =
                        String(value, Charsets.US_ASCII)

                    FIRMWARE_REVISION_CHAR_UUID -> _deviceFirmwareVersion.value =
                        String(value, Charsets.US_ASCII)

                    HARDWARE_REVISION_CHAR_UUID -> _deviceHardwareVersion.value =
                        String(value, Charsets.US_ASCII)

                    SOFTWARE_REVISION_CHAR_UUID -> _deviceSoftwareVersion.value =
                        String(value, Charsets.US_ASCII)

                    MANUFACTURER_CHAR_UUID -> _deviceManufacturer.value =
                        String(value, Charsets.US_ASCII)
                }

            }
        } catch (e: Exception) {
            peripheral.disconnect()
            _error.value = "Reader connection failed"
            Timber.e(e, "Failed initializing device")
        }
    }

    suspend fun storeDevice() {
        if (_deviceModel.value != null &&
            _deviceSerialNumber.value != null &&
            _deviceFirmwareVersion.value != null &&
            _deviceHardwareVersion.value != null &&
            _deviceSoftwareVersion.value != null &&
            _batteryLevel.value != null &&
            _deviceManufacturer.value != null &&
            _bootloaderVersion.value != null &&
            _applicationVersion.value != null
        ) {
            _connectedDevice.emit(
                Device(
                    model = _deviceModel.value!!,
                    serialNumber = _deviceSerialNumber.value!!.toInt(),
                    firmwareVersion = _deviceFirmwareVersion.value!!,
                    hardwareVersion = _deviceHardwareVersion.value!!,
                    softwareVersion = _deviceSoftwareVersion.value!!,
                    bootloaderVersion = _bootloaderVersion.value!!,
                    applicationVersion = _applicationVersion.value!!,
                    batteryPercentage = _batteryLevel.value!!,
                    manufacturer = _deviceManufacturer.value!!,
                )
            )
        }
    }

    @SuppressLint("LongMethod")
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
            _deviceName.value = it.name
            globalSettings.mrd5DeviceMac = it.identifier
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
                observationExceptionHandler { e ->
                    Timber.e(e, "Error in peripheral observation")
                }
            }
            try {
                connectionScope = peripheral.connect()
            } catch (e: Exception) {
                Timber.e(e, "Error connecting to reader")
                _error.value = "Reader connection failed"
                peripheral.disconnect()
                return
            }

            connectionScope?.launch {
                try {
                    var attempts = 0
                    val maxAttempts = 15
                    while (_batteryLevel.value == null && ++attempts <= maxAttempts) {
                        Timber.d("Wait for battery level - attempt $attempts/$maxAttempts")
                        delay(1.seconds)
                    }
                    if (attempts <= maxAttempts) {
                        Timber.d("Got battery level")
                    } else {
                        Timber.w("Timed out getting battery status")
                        _error.value = "Reader connection failed"
                        peripheral.disconnect()
                    }
                    _connectionState.value = ConnectionState.WaitingForPairing

                    attempts = 0
                    while ((_applicationVersion.value == null || _bootloaderVersion.value == null) && ++attempts <= maxAttempts) {
                        sendCommands(listOf(Mrd5Command.Version))
                        Timber.d("Wait for version - attempt $attempts/$maxAttempts")
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
                } catch (e: Exception) {
                    Timber.e(e, "Error connecting to reader")
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

    fun disconnect() {
        managerScope.launch {
            peripheral.disconnect()
        }
    }

    fun sendCommands(commands: List<Mrd5Command>) {
        connectionScope?.launch {
            try {
                val response = peripheral.write(
                    characteristicOf(
                        service = MLDP_SERVICE_UUID,
                        characteristic = MLDP_DATA_UUID,
                    ),
                    data = Mrd5Command.combined(commands).toByteArray(), WriteType.WithResponse
                )
                Timber.d("Command response: $response")
            } catch (e: Exception) {
                Timber.e(e, "Error sending command")
            }
        }
    }

    fun doSuccessChirp() {
        sendCommands(
            listOf(
                Mrd5Command.Tone(Mrd5Tone.Ascending),
                Mrd5Command.LED("070", 400.milliseconds)
            )
        )
    }

    fun doErrorChirp() = sendCommands(
        listOf(
            Mrd5Command.Tone(Mrd5Tone.Warble),
            Mrd5Command.LED("700", 400.milliseconds)
        )
    )


    fun doFindReader() {
        try {
            connectionScope?.launch {
                repeat(3) {
                    sendCommands(
                        listOf(
                            Mrd5Command.Tone(Mrd5Tone.Warble),
                            Mrd5Command.LED("777", 1500.milliseconds),
                        )
                    )
                    delay(1500.milliseconds)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error sending find reader command")
        }
    }

    private val rxBuffer = StringBuilder()
    private var debounceJob: Job? = null

    private fun handleRx(bytes: ByteArray) {
        rxBuffer.append(String(bytes, Charsets.US_ASCII))
        Timber.d("MRD5 RX chunk (buffer: '${rxBuffer.toString().trim()}')")

        debounceJob?.cancel()
        debounceJob = managerScope.launch {
            delay(80L.milliseconds)
            val raw = rxBuffer.toString().trim()
            rxBuffer.clear()
            if (raw.isEmpty()) {
                return@launch
            }

            val transmissions = Mrd5Transmission.fromString(raw)
            transmissions.forEach { transmission ->
                when (transmission) {
                    is Mrd5Transmission.BuzzCard -> {
                        _buzzCardTaps.emit(transmission.tap)
                    }

                    is Mrd5Transmission.BatteryLevel -> {
                        _batteryLevel.value = transmission.level
                    }

                    is Mrd5Transmission.DeviceInfo -> {
                        _bootloaderVersion.value = transmission.bootloaderVersion
                        _applicationVersion.value = transmission.applicationVersion
                    }

                    is Mrd5Transmission.GenericResponse -> {
                        Timber.d("handleRx - generic response: $transmission")
                    }

                    is Mrd5Transmission.Unknown -> {
                        // This is a slightly hacky way to only play the error chirp for transmissions related to a
                        // clobbered BuzzCard read, since there can be other unknown transmissions
                        // shortly after the device connects
                        if (transmission.str.contains("DESFire") || transmission.str.contains("Mobile")) {
                            doErrorChirp()
                        }
                    }
                }
                Timber.d("handleRx - transmission is $transmission")
            }
        }
    }
}

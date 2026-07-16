package org.robojackets.apiary.base.ui.mrd5

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.robojackets.apiary.base.GlobalSettings
import timber.log.Timber
import java.util.ArrayDeque
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

val MLDP_SERVICE_UUID: UUID = UUID.fromString("00035b03-58e6-07dd-021a-08123a000300")
val MLDP_DATA_UUID: UUID = UUID.fromString("00035b03-58e6-07dd-021a-08123a000301")
val CCCD_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

private const val DEBOUNCE_MS = 80L
private const val STORED_CONNECT_TIMEOUT_MS = 10_000L
private const val AUTO_RECONNECT_DELAY_MS = 3_000L

sealed class Mrd5State {
    object Idle : Mrd5State()
    // Attempting direct connection to a previously paired device
    data class TryingStoredDevice(val name: String) : Mrd5State()
    // Scanning for all nearby MRD5 devices
    object Scanning : Mrd5State()
    // Scan has populated the device list; user needs to pick
    object PickDevice : Mrd5State()
    object Connecting : Mrd5State()
    object Connected : Mrd5State()
    object Ready : Mrd5State()
    data class Error(val message: String) : Mrd5State()
}

@Singleton
class Mrd5Manager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val globalSettings: GlobalSettings,
) {
    private val managerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _state = MutableStateFlow<Mrd5State>(Mrd5State.Idle)
    val state: StateFlow<Mrd5State> = _state.asStateFlow()

    // Live list of devices found during the current scan session
    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices.asStateFlow()

    private val _cardData = MutableSharedFlow<CardRead>(extraBufferCapacity = 8)
    val cardData: SharedFlow<CardRead> = _cardData.asSharedFlow()

    private val _lastCardRead = MutableStateFlow<CardRead?>(null)
    val lastCardRead: StateFlow<CardRead?> = _lastCardRead.asStateFlow()

    private val _log = MutableStateFlow<List<String>>(emptyList())
    val log: StateFlow<List<String>> = _log.asStateFlow()

    private var gatt: BluetoothGatt? = null
    private val opQueue = ArrayDeque<() -> Unit>()
    private var opPending = false
    private val rxBuffer = StringBuilder()
    private var debounceJob: Job? = null
    private var storedConnectTimeoutJob: Job? = null
    // True while we are attempting the initial connect to the stored MAC.
    // Used to decide whether a failed connect should fall back to scanning.
    private var attemptingStoredDevice = false
    // True when disconnect was user-initiated (so we don't auto-reconnect)
    private var userDisconnected = false

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
    }

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Called on app start. Silently tries to reconnect to the stored device if
     * one exists. Does nothing if no device is stored.
     */
    fun autoConnect() {
        if (state.value !is Mrd5State.Idle) return
        val mac = globalSettings.mrd5DeviceMac
        if (mac.isNotEmpty()) {
            tryStoredDevice(mac, globalSettings.mrd5DeviceName)
        }
    }

    /**
     * Called from the MRD5 screen. Tries the stored device first; if none is
     * stored (or the attempt fails), falls back to scanning so the user can pick.
     */
    fun startConnection() {
        if (state.value !is Mrd5State.Idle && state.value !is Mrd5State.Error) return
        val mac = globalSettings.mrd5DeviceMac
        if (mac.isNotEmpty()) {
            tryStoredDevice(mac, globalSettings.mrd5DeviceName)
        } else {
            startScan()
        }
    }

    /** Start a fresh scan, ignoring any stored device. */
    fun startScan() {
        cancelStoredDeviceTimeout()
        stopScan()
        val scanner = bluetoothAdapter?.bluetoothLeScanner ?: run {
            updateState(Mrd5State.Error("Bluetooth not available"))
            return
        }
        _discoveredDevices.value = emptyList()
        updateState(Mrd5State.Scanning)
        log("Scanning for MRD5 readers...")

        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(MLDP_SERVICE_UUID))
            .build()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        scanner.startScan(listOf(filter), settings, scanCallback)
    }

    /** Connect to a device chosen from the picker. Persists the choice. */
    @SuppressLint("MissingPermission")
    fun connectTo(device: BluetoothDevice) {
        stopScan()
        globalSettings.mrd5DeviceMac = device.address
        globalSettings.mrd5DeviceName = device.name ?: device.address
        userDisconnected = false
        connectGatt(device)
    }

    /** Forget the stored device and disconnect. */
    @SuppressLint("MissingPermission")
    fun forgetAndDisconnect() {
        globalSettings.mrd5DeviceMac = ""
        globalSettings.mrd5DeviceName = ""
        disconnect(userInitiated = true)
    }

    @SuppressLint("MissingPermission")
    fun disconnect(userInitiated: Boolean = true) {
        userDisconnected = userInitiated
        cancelStoredDeviceTimeout()
        stopScan()
        debounceJob?.cancel()
        opQueue.clear()
        opPending = false
        rxBuffer.clear()

        // Capture and immediately null the reference so onConnectionStateChange
        // knows we already handled cleanup and can skip its own close() call.
        val currentGatt = gatt
        gatt = null

        if (currentGatt != null) {
            log("Sending BLE disconnect…")
            try {
                currentGatt.disconnect()
            } catch (e: Exception) {
                Timber.e(e, "Exception calling gatt.disconnect()")
            }
            // Delay close() so the BLE stack has time to deliver the HCI disconnect
            // to the remote device before we release the GATT client resources.
            // Calling close() too quickly can abort the outbound packet, which is
            // why the MRD5 previously showed no LED/chime acknowledgement.
            managerScope.launch {
                delay(500L)
                try {
                    currentGatt.close()
                    log("GATT client released")
                } catch (e: Exception) {
                    Timber.e(e, "Exception calling gatt.close()")
                }
            }
        } else {
            log("disconnect() called but gatt was already null")
        }

        updateState(Mrd5State.Idle)
        log(if (userInitiated) "Disconnected" else "Connection lost")
    }

    // ── Internal helpers ───────────────────────────────────────────────────────

    @SuppressLint("MissingPermission")
    private fun tryStoredDevice(mac: String, name: String) {
        val device = try {
            bluetoothAdapter?.getRemoteDevice(mac)
        } catch (e: IllegalArgumentException) {
            null
        }
        if (device == null) {
            log("Stored MAC $mac invalid — scanning instead")
            startScan()
            return
        }
        val displayName = name.ifEmpty { mac }
        updateState(Mrd5State.TryingStoredDevice(displayName))
        log("Trying previously connected reader: $displayName")
        attemptingStoredDevice = true
        userDisconnected = false

        // Fall back to scanning if the stored device doesn't connect within timeout
        storedConnectTimeoutJob = managerScope.launch {
            delay(STORED_CONNECT_TIMEOUT_MS)
            if (state.value is Mrd5State.TryingStoredDevice || state.value is Mrd5State.Connecting) {
                log("$displayName not found within ${STORED_CONNECT_TIMEOUT_MS / 1000}s — scanning")
                attemptingStoredDevice = false
                gatt?.close()
                gatt = null
                startScan()
            }
        }
        connectGatt(device)
    }

    @SuppressLint("MissingPermission")
    private fun connectGatt(device: BluetoothDevice) {
        updateState(Mrd5State.Connecting)
        log("Connecting to ${device.address}…")
        gatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    @SuppressLint("MissingPermission")
    private fun stopScan() {
        try {
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
        } catch (_: Exception) { }
    }

    private fun cancelStoredDeviceTimeout() {
        storedConnectTimeoutJob?.cancel()
        storedConnectTimeoutJob = null
    }

    // ── GATT op queue ──────────────────────────────────────────────────────────

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

    // ── Scan callback ──────────────────────────────────────────────────────────

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val current = _discoveredDevices.value
            if (current.none { it.address == device.address }) {
                val name = device.name ?: device.address
                log("Found: $name (${device.address})")
                _discoveredDevices.value = current + device
            }
            // Transition to PickDevice as soon as we have at least one result
            if (state.value is Mrd5State.Scanning) {
                updateState(Mrd5State.PickDevice)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            updateState(Mrd5State.Error("Scan failed: error $errorCode"))
        }
    }

    // ── GATT callbacks ─────────────────────────────────────────────────────────

    @SuppressLint("MissingPermission")
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    cancelStoredDeviceTimeout()
                    attemptingStoredDevice = false
                    log("Connected. Requesting high priority + discovering services…")
                    updateState(Mrd5State.Connected)
                    gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)
                    gatt.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    cancelStoredDeviceTimeout()
                    val wasStoredAttempt = attemptingStoredDevice
                    attemptingStoredDevice = false
                    debounceJob?.cancel()
                    opQueue.clear()
                    opPending = false
                    rxBuffer.clear()
                    // Only close here for unmanaged disconnects. For user-initiated
                    // disconnects, gatt was already nulled and close() is on a 500ms
                    // delay in disconnect() — skip close here to avoid a double-close.
                    val currentGatt = this@Mrd5Manager.gatt
                    this@Mrd5Manager.gatt = null
                    currentGatt?.close()

                    when {
                        wasStoredAttempt -> {
                            log("Stored reader not reachable — scanning for nearby readers")
                            startScan()
                        }
                        userDisconnected -> {
                            updateState(Mrd5State.Idle)
                            log("Disconnected")
                        }
                        else -> {
                            val mac = globalSettings.mrd5DeviceMac
                            log("Disconnected unexpectedly (status=$status)")
                            updateState(Mrd5State.Idle)
                            if (mac.isNotEmpty()) {
                                managerScope.launch {
                                    delay(AUTO_RECONNECT_DELAY_MS)
                                    log("Attempting auto-reconnect…")
                                    tryStoredDevice(mac, globalSettings.mrd5DeviceName)
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                updateState(Mrd5State.Error("Service discovery failed: $status"))
                return
            }
            val dataChar = gatt.getService(MLDP_SERVICE_UUID)
                ?.getCharacteristic(MLDP_DATA_UUID)
            if (dataChar == null) {
                updateState(Mrd5State.Error("MLDP data characteristic not found"))
                return
            }
            log("Services discovered. Writing CCCD immediately (race vs GPIO4)…")
            gatt.setCharacteristicNotification(dataChar, true)
            enqueue {
                val cccd = dataChar.getDescriptor(CCCD_UUID)
                if (cccd == null) {
                    updateState(Mrd5State.Error("CCCD descriptor not found"))
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
                log("CCCD write confirmed — ready for card reads")
                updateState(Mrd5State.Ready)
            } else {
                log("CCCD write failed: status=$status")
                updateState(Mrd5State.Error("CCCD write failed (status=$status)"))
            }
            opComplete()
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
        ) {
            if (characteristic.uuid == MLDP_DATA_UUID) handleRx(value)
        }

        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
        ) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (characteristic.uuid == MLDP_DATA_UUID) handleRx(characteristic.value)
            }
        }
    }

    // ── Incoming data with debounced reassembly ────────────────────────────────

    private fun handleRx(bytes: ByteArray) {
        rxBuffer.append(String(bytes, Charsets.US_ASCII))
        Timber.d("MRD5 RX chunk (buffer: '${rxBuffer.toString().trim()}')")
        debounceJob?.cancel()
        debounceJob = managerScope.launch {
            delay(DEBOUNCE_MS)
            val raw = rxBuffer.toString().trim()
            rxBuffer.clear()
            if (raw.isEmpty()) return@launch
            val cardRead = raw.toCardRead(org.robojackets.apiary.base.ui.nfc.BuzzCardTapSource.Mrd5)
            if (cardRead != null) {
                log("Card read: $raw")
                _lastCardRead.value = cardRead
                _cardData.tryEmit(cardRead)
            } else {
                log("Unrecognised data: $raw")
            }
        }
    }

    // ── GATT write helpers ─────────────────────────────────────────────────────

    @SuppressLint("MissingPermission")
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

    private fun updateState(s: Mrd5State) { _state.value = s }

    private fun log(message: String) {
        Timber.d("Mrd5Manager: $message")
        val updated = _log.value.toMutableList().also {
            it.add(0, message)
            if (it.size > 50) it.removeAt(it.size - 1)
        }
        _log.value = updated
    }
}

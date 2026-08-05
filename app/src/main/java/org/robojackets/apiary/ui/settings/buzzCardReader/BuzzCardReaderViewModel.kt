package org.robojackets.apiary.ui.settings.buzzCardReader

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import org.robojackets.apiary.base.GlobalSettings
import org.robojackets.apiary.base.ui.bluetooth.BleManager
import org.robojackets.apiary.base.ui.bluetooth.ConnectionState
import org.robojackets.apiary.navigation.NavigationManager
import javax.inject.Inject


// BLE logic based on https://medium.com/@YodgorbekKomilo/designing-a-robust-ble-system-in-android-with-jetpack-compose-a7941bec8c66
@HiltViewModel
class BuzzCardReaderViewModel @Inject constructor(
    @Suppress("UnusedPrivateMember") private val savedStateHandle: SavedStateHandle,
    val globalSettings: GlobalSettings,
    val navigationManager: NavigationManager,
    val bleManager: BleManager,
) : ViewModel() {
    val devices = bleManager.scanResults
        .map { it.device }
        .scan(emptySet<BluetoothDevice>()) { acc, device -> acc + device }
        .map { it.toList() }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val connection = bleManager.connectionState
        .stateIn(viewModelScope, SharingStarted.Lazily, ConnectionState.Disconnected)

    fun startScan() = bleManager.startScan()
    fun connect(address: String) = bleManager.connect(address) // FIXME
}

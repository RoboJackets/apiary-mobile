package org.robojackets.apiary.ui.settings.buzzCardReader

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juul.kable.Advertisement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.robojackets.apiary.base.GlobalSettings
import org.robojackets.apiary.base.repository.DeviceRepository
import org.robojackets.apiary.base.ui.bluetooth.ConnectionState
import org.robojackets.apiary.base.ui.bluetooth.Mrd5Manager
import org.robojackets.apiary.navigation.NavigationManager
import javax.inject.Inject

@HiltViewModel
class BuzzCardReaderViewModel @Inject constructor(
    @Suppress("UnusedPrivateMember") private val savedStateHandle: SavedStateHandle,
    val globalSettings: GlobalSettings,
    val navigationManager: NavigationManager,
    val mrd5Manager: Mrd5Manager,
    val deviceRepository: DeviceRepository,
) : ViewModel() {
    val devices = mrd5Manager.scanResults
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val connection = mrd5Manager.connectionState
        .stateIn(viewModelScope, SharingStarted.Lazily, ConnectionState.Disconnected)

    val batteryLevel = mrd5Manager.batteryLevel
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val buzzCardTaps = mrd5Manager.buzzCardTaps
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        viewModelScope.launch {
            mrd5Manager.connectedDevice.collect {
                it?.let {
                    deviceRepository.inventoryDevice(it)
                }
            }
        }
    }

    fun startScan() = mrd5Manager.startScanning(viewModelScope)

    fun resetMrd5Error() = mrd5Manager.resetError()

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    fun connect(advertisement: Advertisement?) = viewModelScope.launch {
        mrd5Manager.connect(advertisement)
    }

    fun disconnect(isUserDisconnect: Boolean) = mrd5Manager.disconnect(isUserDisconnect = isUserDisconnect)

    fun findReader() = mrd5Manager.doFindReader()
}

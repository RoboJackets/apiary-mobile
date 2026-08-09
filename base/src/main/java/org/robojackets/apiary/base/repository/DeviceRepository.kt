package org.robojackets.apiary.base.repository

import com.skydoves.sandwich.ApiResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import org.robojackets.apiary.base.model.Device
import org.robojackets.apiary.base.model.DeviceHolder
import org.robojackets.apiary.base.service.DeviceService
import javax.inject.Inject

@ActivityRetainedScoped
class DeviceRepository @Inject constructor(
    val deviceService: DeviceService
) {
    suspend fun inventoryDevice(device: Device): ApiResponse<DeviceHolder> {
        return deviceService.inventoryDevice(
            model = device.model,
            serialNumber = device.serialNumber,
            hardwareVersion = device.hardwareVersion,
            firmwareVersion = device.firmwareVersion,
            softwareVersion = device.softwareVersion,
            batteryPercentage = device.batteryPercentage,
            manufacturer = device.manufacturer,
        )
    }
}

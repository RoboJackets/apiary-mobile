package org.robojackets.apiary.base.service

import com.skydoves.sandwich.ApiResponse
import org.robojackets.apiary.base.model.DeviceHolder
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface DeviceService {
    @FormUrlEncoded
    @POST("/api/v1/devices/inventory")
    suspend fun inventoryDevice(
        @Field("model") model: String,
        @Field("serial_number") serialNumber: Int,
        @Field("hardware_version") hardwareVersion: String,
        @Field("bluetooth_firmware_version") firmwareVersion: String,
        @Field("bluetooth_software_version") softwareVersion: String,
        @Field("bootloader_version") bootloaderVersion: String,
        @Field("application_version") applicationVersion: String,
        @Field("battery_percentage") batteryPercentage: Int,
        @Field("manufacturer") manufacturer: String,
    ): ApiResponse<DeviceHolder>
}

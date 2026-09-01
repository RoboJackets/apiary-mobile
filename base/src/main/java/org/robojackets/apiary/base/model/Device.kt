package org.robojackets.apiary.base.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeviceHolder(
    val status: String,
    val device: Device,
)

@JsonClass(generateAdapter = true)
data class Device(
    val manufacturer: String,
    val model: String,
    @Json(name = "serial_number")
    val serialNumber: Int,
    @Json(name = "hardware_version")
    val hardwareVersion: String,
    @Json(name = "bluetooth_firmware_version")
    val firmwareVersion: String,
    @Json(name = "bluetooth_software_version")
    val softwareVersion: String,
    @Json(name = "bootloader_version")
    val bootloaderVersion: String,
    @Json(name = "application_version")
    val applicationVersion: String,
    @Json(name = "battery_percentage")
    val batteryPercentage: Int,
)

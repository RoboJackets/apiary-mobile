package org.robojackets.apiary.base.model

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
    val serialNumber: Int,
    val hardwareVersion: String,
    val firmwareVersion: String,
    val softwareVersion: String,
    val bootloaderVersion: String,
    val applicationVersion: String,
    val batteryPercentage: Int,
)

package org.robojackets.apiary.base.service

import com.skydoves.sandwich.ApiResponse
import org.robojackets.apiary.base.model.Device
import org.robojackets.apiary.base.model.DeviceHolder
import retrofit2.http.Body
import retrofit2.http.POST

interface DeviceService {
    @POST("/api/v1/devices/inventory")
    suspend fun inventoryDevice(@Body device: Device): ApiResponse<DeviceHolder>
}

package org.robojackets.apiary.attendance.network

import com.skydoves.sandwich.ApiResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.robojackets.apiary.base.model.AttendanceHolder
import org.robojackets.apiary.base.model.Device
import retrofit2.http.Body
import retrofit2.http.POST

interface AttendanceApiService {
    @JsonClass(generateAdapter = true)
    data class RecordAttendanceRequest(
        @Json(name = "attendable_type")
        val attendableType: String,
        @Json(name = "attendable_id")
        val attendableId: Int,
        val gtid: Int,
        val source: String,
        val reader: Device?,
    )

    @POST("/api/v1/attendance?include=attendee")
    suspend fun recordAttendance(
        @Body body: RecordAttendanceRequest
    ): ApiResponse<AttendanceHolder>
}

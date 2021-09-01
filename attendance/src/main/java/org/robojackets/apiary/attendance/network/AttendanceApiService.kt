package org.robojackets.apiary.attendance.network

import com.skydoves.sandwich.ApiResponse
import org.robojackets.apiary.base.model.AttendanceHolder
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AttendanceApiService {
    @FormUrlEncoded
    @POST("/api/v1/attendance?include=attendee")
    suspend fun recordAttendance(
        @Field("attendable_type") attendableType: String,
        @Field("attendable_id") attendableId: Int,
        @Field("gtid") gtid: Int,
        @Field("source") source: String
    ): ApiResponse<AttendanceHolder>
}
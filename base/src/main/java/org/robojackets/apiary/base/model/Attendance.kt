package org.robojackets.apiary.base.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttendanceHolder(
    val attendance: Attendance
)

@JsonClass(generateAdapter = true)
data class Attendance(
    val attendee: BasicUser?
)

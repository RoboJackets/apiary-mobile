package org.robojackets.apiary.attendance.network

import dagger.hilt.android.scopes.ActivityRetainedScoped
import org.robojackets.apiary.base.model.Device
import javax.inject.Inject

@ActivityRetainedScoped
class AttendanceRepository @Inject constructor(
    val attendanceApiService: AttendanceApiService,
) {
    suspend fun recordAttendance(
        attendableType: String,
        attendableId: Int,
        gtid: Int,
        source: String = "MyRoboJackets Android",
        reader: Device?
    ) = attendanceApiService.recordAttendance(
        AttendanceApiService.RecordAttendanceRequest(
            attendableType,
            attendableId,
            gtid,
            source,
            reader
        )
    )
}

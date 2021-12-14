package org.robojackets.apiary.attendance.network

import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class AttendanceRepository @Inject constructor(
    val attendanceApiService: AttendanceApiService,
) {
    suspend fun recordAttendance(
        attendableType: String,
        attendableId: Int,
        gtid: Int,
        source: String = "MyRoboJackets Android"
    ) = attendanceApiService.recordAttendance(
        attendableType,
        attendableId,
        gtid,
        source
    )
}

package org.robojackets.apiary.attendance.network

import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class AttendanceRepository @Inject constructor(
    val attendanceApiService: AttendanceApiService,
) {
    suspend fun recordAttendance( // FIXME also include MRD5 device info when available
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

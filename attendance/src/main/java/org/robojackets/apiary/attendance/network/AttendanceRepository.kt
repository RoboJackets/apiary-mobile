package org.robojackets.apiary.attendance.network

import dagger.hilt.android.scopes.ActivityRetainedScoped
import org.robojackets.apiary.base.ui.mrd5.CardRead
import javax.inject.Inject

@ActivityRetainedScoped
class AttendanceRepository @Inject constructor(
    val attendanceApiService: AttendanceApiService,
) {
    private val source = "MyRoboJackets Android"

    suspend fun recordAttendance(
        attendableType: String,
        attendableId: Int,
        cardRead: CardRead,
    ) = when (cardRead) {
        is CardRead.Gtid -> attendanceApiService.recordAttendanceByGtid(
            attendableType,
            attendableId,
            cardRead.gtid,
            "$source - ${cardRead.source}",
        )
        is CardRead.AccessCardNumber -> attendanceApiService.recordAttendanceByAccessCardNumber(
            attendableType,
            attendableId,
            cardRead.number,
            "$source - ${cardRead.source}",
        )
    }
}

package org.robojackets.apiary.attendance.model

import org.robojackets.apiary.base.ui.mrd5.CardRead

data class AttendanceStoreResult(
    val cardRead: CardRead,
    val success: Boolean = true,
    val name: String? = null,
    val message: String? = null,
)

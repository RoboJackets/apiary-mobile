package org.robojackets.apiary.attendance.model

import org.robojackets.apiary.base.ui.nfc.BuzzCardTap

data class AttendanceStoreResult(
    val tap: BuzzCardTap,
    val success: Boolean = true,
    val name: String? = null,
    val message: String? = null,
)

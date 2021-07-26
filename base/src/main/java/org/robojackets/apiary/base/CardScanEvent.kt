package org.robojackets.apiary.base

import java.sql.Timestamp
import java.util.*

data class CardScanEvent(
    val gtid: String,
    val timestamp: Date = Timestamp(System.currentTimeMillis())
)
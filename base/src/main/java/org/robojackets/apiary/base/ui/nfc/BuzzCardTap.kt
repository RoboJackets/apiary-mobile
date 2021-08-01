package org.robojackets.apiary.base.ui.nfc

import java.time.Instant

data class BuzzCardTap(
    val gtid: Int,
    val timestamp: Instant = Instant.now()
)

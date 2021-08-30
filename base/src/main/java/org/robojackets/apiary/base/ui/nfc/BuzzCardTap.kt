package org.robojackets.apiary.base.ui.nfc

import org.robojackets.apiary.base.ui.nfc.BuzzCardTapSource.Nfc
import java.time.Instant

data class BuzzCardTap(
    val gtid: Int,
    val timestamp: Instant = Instant.now(),
    val source: BuzzCardTapSource = Nfc,
)

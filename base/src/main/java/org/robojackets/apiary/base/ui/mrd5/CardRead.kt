package org.robojackets.apiary.base.ui.mrd5

import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.base.ui.nfc.BuzzCardTapSource
import org.robojackets.apiary.base.ui.nfc.GTID_REGEX

/**
 * Represents a card read from any source (NFC, MRD5 BLE, keyboard).
 *
 * The MRD5 reader may return either a GTID (9 digits starting with 90) or a
 * Blackboard access card number (16-digit PAN), depending on how the individual
 * reader is configured. The server accepts both via different POST body fields:
 * `gtid` for GTIDs, `access_card_number` for PANs.
 */
sealed class CardRead {
    /** Unique string identifier for deduplication across both variants. */
    abstract val identifier: String
    abstract val source: BuzzCardTapSource

    data class Gtid(
        val gtid: Int,
        override val source: BuzzCardTapSource,
    ) : CardRead() {
        override val identifier = gtid.toString()
    }

    data class AccessCardNumber(
        val number: String,
        override val source: BuzzCardTapSource,
    ) : CardRead() {
        override val identifier = number
    }
}

/**
 * Parse a raw string from the MRD5 MLDP pipe into a typed [CardRead].
 * Returns null if the string is empty or unrecognisable.
 */
fun String.toCardRead(source: BuzzCardTapSource): CardRead? {
    val cleaned = this.trim()
    return when {
        GTID_REGEX.matches(cleaned) -> CardRead.Gtid(cleaned.toInt(), source)
        cleaned.isNotEmpty() -> CardRead.AccessCardNumber(cleaned, source)
        else -> null
    }
}

/** Convert an NFC-sourced [BuzzCardTap] into a [CardRead.Gtid]. */
fun BuzzCardTap.toCardRead(): CardRead.Gtid = CardRead.Gtid(gtid, source)

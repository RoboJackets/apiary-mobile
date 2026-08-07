package org.robojackets.apiary.base.ui.bluetooth

import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.base.ui.nfc.BuzzCardTapSource
import timber.log.Timber

private fun parseBatteryLevel(str: String): Int? {
    val regex = Regex("BATT:(\\d{1,3})/\\d+")
    regex.matchEntire(str)?.let { matchResult ->
        matchResult.groups.lastOrNull()?.let {
            return it.value.toInt()
        }
    }
    return null
}

private fun parseBuzzCardTap(str: String): BuzzCardTap? {
    val regex = Regex("""(90[0-9]{7})\|\d+\|(\w+)""")
    regex.matchEntire(str)?.let { matchResult ->
        // FIXME: Check groups length
        val gtid = matchResult.groups[1]?.value?.toInt()
        val source = matchResult.groups.lastOrNull()?.value

        if (gtid == null || source == null) {
            return null
        }

        return BuzzCardTap(
            gtid = gtid,
            source = BuzzCardTapSource.Mrd5(source)
        )
    }
    return null
}

sealed interface Mrd5Transmission {
    data class BuzzCard(val tap: BuzzCardTap) : Mrd5Transmission
    data class BatteryLevel(val level: Int): Mrd5Transmission

    companion object {
        fun fromString(str: String): Mrd5Transmission? {
            try {
                val buzzCardTap = parseBuzzCardTap(str)
                if (buzzCardTap != null) {
                    return BuzzCard(buzzCardTap)
                }

                val batteryLevel = parseBatteryLevel(str)
                if (batteryLevel != null) {
                    return BatteryLevel(batteryLevel)
                }
            } catch (e: NumberFormatException) {
                Timber.e(e, "Error processing MRD5 transmission")
            } catch (e: IndexOutOfBoundsException) {
                Timber.e(e, "Error processing MRD5 transmission")
            }

            Timber.w("Unrecognized MRD5 transmission: $str")
            return null
        }
    }
}
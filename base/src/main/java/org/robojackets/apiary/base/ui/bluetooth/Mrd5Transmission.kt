package org.robojackets.apiary.base.ui.bluetooth

import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.base.ui.nfc.BuzzCardTapSource
import timber.log.Timber

private fun parseBatteryLevel(str: String): Int? {
    val regex = Regex("""BATT:(?<batteryLevel>\d{1,3})/-?\d+""")
    regex.matchEntire(str)?.let { matchResult ->
        matchResult.groups["batteryLevel"]?.let {
            return it.value.toInt()
        }
    }
    return null
}

private fun parseBuzzCardTap(str: String): BuzzCardTap? {
    val regex = Regex("""(?<gtid>90[0-9]{7})\|\d*\|(?<source>\w+)""")
    regex.matchEntire(str)?.let { matchResult ->
        val gtid = matchResult.groups["gtid"]?.value?.toInt()
        val source = matchResult.groups["source"]?.value

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

@Suppress("MagicNumber")
private fun parseDeviceInfo(str: String): Mrd5Transmission.DeviceInfo? {
    val regex = Regex("""Blackboard MRD5\r\n\s*SN: (?<sn>\d+)\r\n\s*Boot: (?<boot>.+)\r\n\s*Application: (?<application>.+)""")
    regex.matchEntire(str)?.let { matchResult ->
        if (matchResult.groups.size == 4) {
            val serialNumber = matchResult.groups["sn"]?.value
            val bootloaderVersion = matchResult.groups["boot"]?.value
            val applicationVersion = matchResult.groups["application"]?.value

            if (serialNumber != null && bootloaderVersion != null && applicationVersion != null) {
                return Mrd5Transmission.DeviceInfo(
                    serialNumber = serialNumber,
                    bootloaderVersion = bootloaderVersion,
                    applicationVersion = applicationVersion,
                )
            }
        }
    }
    return null
}

private fun parseGenericResponse(str: String): Mrd5Transmission.GenericResponse? {
    if (str == "LED on") {
        return Mrd5Transmission.GenericResponse(str)
    }

    val regex = Regex("Tone = .")
    regex.matchEntire(str)?.let { matchResult ->
        return Mrd5Transmission.GenericResponse(str)
    }
    return null
}

sealed interface Mrd5Transmission {
    data class BuzzCard(val tap: BuzzCardTap) : Mrd5Transmission
    data class BatteryLevel(val level: Int) : Mrd5Transmission

    data class GenericResponse(val str: String) : Mrd5Transmission
    data class DeviceInfo(
        val serialNumber: String,
        val bootloaderVersion: String,
        val applicationVersion: String,
    ) : Mrd5Transmission

    data class Unknown(val str: String) : Mrd5Transmission

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    companion object {
        fun fromString(str: String): List<Mrd5Transmission> {
            val deviceInfo = parseDeviceInfo(str)
            if (deviceInfo != null) {
                return listOf(deviceInfo)
            }

            val chunks = str.split("\r\n")
            val result = mutableListOf<Mrd5Transmission>()
            for (chunk in chunks) {
                try {
                    val buzzCardTap = parseBuzzCardTap(chunk)
                    if (buzzCardTap != null) {
                        result.add(BuzzCard(buzzCardTap))
                        continue
                    }

                    val batteryLevel = parseBatteryLevel(chunk)
                    if (batteryLevel != null) {
                        result.add(BatteryLevel(batteryLevel))
                        continue
                    }

                    val genericResponse = parseGenericResponse(chunk)
                    if (genericResponse != null) {
                        result.add(genericResponse)
                        continue
                    }
                } catch (e: NumberFormatException) {
                    Timber.e(e, "Error processing MRD5 transmission")
                } catch (e: IndexOutOfBoundsException) {
                    Timber.e(e, "Error processing MRD5 transmission")
                }

                Timber.w("Unrecognized MRD5 transmission: $str")
                result.add(Unknown(str))
            }
            return result
        }
    }
}

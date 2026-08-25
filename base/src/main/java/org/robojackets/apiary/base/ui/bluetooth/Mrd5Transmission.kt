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

@Suppress("MagicNumber")
private fun parseDeviceInfo(str: String): Mrd5Transmission.DeviceInfo? {
    val regex = Regex("""Blackboard MRD5\r\n\s*SN: (\d+)\r\n\s*Boot: (.+)\r\n\s*Application: (.+)""")
    regex.matchEntire(str)?.let { matchResult ->
        if (matchResult.groups.size == 4) {
            val serialNumber = matchResult.groups[1]?.value
            val bootloaderVersion = matchResult.groups[2]?.value
            val applicationVersion = matchResult.groups[3]?.value

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

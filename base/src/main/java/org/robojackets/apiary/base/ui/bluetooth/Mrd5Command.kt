package org.robojackets.apiary.base.ui.bluetooth

import kotlin.time.Duration

enum class Mrd5Tone(val key: String) {
    LowHighLow("L"),
    HighLowHigh("H"),
    Warble("W"),
    Single("K"),
    Ascending("A"),
    Descending("D"),
    FourLongBeeps("B"),
    LongTone("V")
}

sealed interface Mrd5Command {
    companion object {
        fun combined(commands: List<Mrd5Command>): String {
            return commands.joinToString(separator = "\n", postfix = "\n") { it.toString() }
        }
    }

    object Version : Mrd5Command {
        override fun toString(): String {
            return "VER:"
        }
    }

    data class Tone(val tone: Mrd5Tone) : Mrd5Command {
        override fun toString(): String {
            return "TONE:${tone.key}"
        }
    }

    data class LED(val color: String, val duration: Duration) : Mrd5Command {
        override fun toString(): String {
            return "LED:$color,${duration.inWholeMilliseconds}"
        }
    }
}

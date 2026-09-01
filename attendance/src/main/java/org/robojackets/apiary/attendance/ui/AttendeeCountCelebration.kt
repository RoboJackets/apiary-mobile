@file:Suppress("MagicNumber")
package org.robojackets.apiary.attendance.ui

import androidx.compose.runtime.Composable
import org.robojackets.apiary.base.ui.util.IconRow

@Composable
fun AttendeeCountCelebration(
    count: Int
) {
    val text = when (count) {
        5 -> "🔥 5 attendees recorded. You're on a roll!"
        10 -> "👑 10 attendees. You're awesome!"
        25 -> "🎸 25 attendees! You're a rockstar!"
        42 -> "4️⃣2️⃣ The meaning of life."
        50 -> "🎉 50 attendees! Is this GI?"
        100 -> "💯 100 ATTENDEES! Go give yourself a prize!"
        else -> null
    }

    text?.let {
        IconRow(
            icon = null,
            text = text,
            button = {},
            showDivider = false,
        )
    }
}

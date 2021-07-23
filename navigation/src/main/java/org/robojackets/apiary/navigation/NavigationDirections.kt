package org.robojackets.apiary.navigation

import androidx.navigation.compose.NamedNavArgument

object NavigationDirections {
    val Authentication = object : NavigationCommand {
        override val arguments = emptyList<NamedNavArgument>()
        override val destination = "authentication"
        override val isInBottomNav = false
    }

    val Attendance = object : NavigationCommand {
        override val arguments = emptyList<NamedNavArgument>()
        override val destination = "attendance"
        override val isInBottomNav = true
    }

    val Settings = object : NavigationCommand {
        override val arguments = emptyList<NamedNavArgument>()
        override val destination = "settings"
        override val isInBottomNav = true
    }
}

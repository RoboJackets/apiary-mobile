package org.robojackets.apiary.navigation

import androidx.navigation.compose.NamedNavArgument

object NavigationDirections {
    val Authentication = object : NavigationCommand {
        override val arguments = emptyList<NamedNavArgument>()
        override val destination = "authentication"
    }

    val Attendance = object : NavigationCommand {
        override val arguments = emptyList<NamedNavArgument>()
        override val destination = "dashboard"
    }
}
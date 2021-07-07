package org.robojackets.apiary.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import org.robojackets.apiary.navigation.NavigationDirections.Authentication

class NavigationManager {
    var commands = MutableStateFlow(Authentication)

    fun navigate(
        directions: NavigationCommand
    ) {
        commands.value = directions
    }
}
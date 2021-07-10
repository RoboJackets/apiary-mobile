package org.robojackets.apiary.navigation

import kotlinx.coroutines.flow.MutableStateFlow

class NavigationManager {
    var commands = MutableStateFlow<NavigationCommand?>(null)

    fun navigate(
        directions: NavigationCommand
    ) {
        commands.value = directions
    }
}
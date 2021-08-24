package org.robojackets.apiary.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {
    var commands = MutableStateFlow<NavigationCommand?>(null)

    fun navigate(directions: NavigationCommand) {
        commands.value = directions
    }
}

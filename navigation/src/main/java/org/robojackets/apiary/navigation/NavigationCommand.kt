package org.robojackets.apiary.navigation

import androidx.navigation.compose.NamedNavArgument

interface NavigationCommand {
    val arguments: List<NamedNavArgument>
    val destination: String
    val isInBottomNav: Boolean
//    val navOptionsBuilder: NavOptionsBuilder TODO: Fully implement this
}

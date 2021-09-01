package org.robojackets.apiary.navigation

import androidx.navigation.NavOptions

// Based on https://proandroiddev.com/how-to-make-jetpack-compose-navigation-easier-and-testable-b4b19fd5f2e4
object NavigationDestinations {
    const val authentication = "authentication"
    const val settings = "settings"
    const val attendableSelection = "attendableSelection"
    const val attendance = "attendance"
}

object NavigationActions {
    object Authentication {
        fun anyScreenToAuthentication() = object : NavigationAction {
            override val destination = NavigationDestinations.authentication
            override val navOptions = NavOptions.Builder()
                .setPopUpTo(0, true)
                .build()
        }
    }

    object BottomNavTabs {
        // graphStartId: example navController.graph.findStartDestination().id
        fun bottomNavTabs(destination: String, graphStartId: Int) = object : NavigationAction {
            override val destination = destination
            override val navOptions = NavOptions.Builder()
                .setPopUpTo(graphStartId, true, saveState = true)
                .setLaunchSingleTop(true)
                .setRestoreState(true)
                .build()
        }
    }

    object Attendance {
        fun authToAttendance() = object : NavigationAction {
            override val destination = NavigationDestinations.attendableSelection
            override val navOptions = NavOptions.Builder()
                .setPopUpTo(0, true)
                .setLaunchSingleTop(true)
                .setRestoreState(true)
                .build()
        }

        fun attendableSelectionToAttendance(
            attendableType: String,
            attendableId: Int,
        ) = object : NavigationAction {
            override val destination = "${NavigationDestinations.attendance}/$attendableType/$attendableId"
            override val navOptions = NavOptions.Builder()
                .setPopUpTo(NavigationDestinations.attendance, inclusive = true)
                .build()
        }

        fun attendanceToAttendableSelection() = object : NavigationAction {
            override val destination = NavigationDestinations.attendableSelection
        }
    }
}

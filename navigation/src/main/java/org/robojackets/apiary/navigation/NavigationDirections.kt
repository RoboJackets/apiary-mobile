package org.robojackets.apiary.navigation

import androidx.navigation.NavOptions

// Based on https://proandroiddev.com/how-to-make-jetpack-compose-navigation-easier-and-testable-b4b19fd5f2e4
object NavigationDestinations {
    const val authentication = "authentication"
    const val settings = "settings"
    const val attendanceSubgraph = "attendance"
    const val attendableTypeSelection = "attendableTypeSelection"
    const val attendableSelection = "attendableSelection"
    const val attendance = "attendanceMain"
    const val optionalUpdatePrompt = "optionalUpdatePrompt"
    const val requiredUpdatePrompt = "requiredUpdatePrompt"
    const val updateInProgress = "updateInProgress"
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
        fun withinBottomNavTabs(destination: String, graphStartId: Int) = object : NavigationAction {
            override val destination = destination
            override val navOptions = NavOptions.Builder()
                .setPopUpTo(graphStartId, false, saveState = true)
                .setLaunchSingleTop(true)
                .setRestoreState(true)
                .build()
        }
    }

    object UpdatePrompts {
        fun anyScreenToOptionalUpdatePrompt() = object : NavigationAction {
            override val destination = NavigationDestinations.optionalUpdatePrompt
        }
        fun anyScreenToRequiredUpdatePrompt() = object : NavigationAction {
            override val destination = NavigationDestinations.requiredUpdatePrompt
            override val navOptions = NavOptions.Builder()
                .setPopUpTo(0, true)
                .build()
        }
        fun anyScreenToUpdateInProgress() = object : NavigationAction {
            override val destination = NavigationDestinations.updateInProgress
            override val navOptions = NavOptions.Builder()
                .setPopUpTo(0, true)
                .build()
        }
    }

    object Attendance {
        fun authToAttendance() = object : NavigationAction {
            override val destination = NavigationDestinations.attendanceSubgraph
            override val navOptions = NavOptions.Builder()
                .setPopUpTo(0, true)
                .setLaunchSingleTop(true)
                .setRestoreState(true)
                .build()
        }

        fun attendableTypeSelectToAttendableSelect(
            attendableType: String
        ) = object : NavigationAction {
            override val destination = "${NavigationDestinations.attendableSelection}/$attendableType"
        }

        fun attendableSelectionToAttendance(
            attendableType: String,
            attendableId: Int,
        ) = object : NavigationAction {
            override val destination = "${NavigationDestinations.attendance}/$attendableType/$attendableId"
        }

        fun attendanceToAttendableTypeSelect() = object : NavigationAction {
            override val destination = NavigationDestinations.attendableTypeSelection
            override val navOptions: NavOptions
                get() = NavOptions.Builder()
                    .setPopUpTo(NavigationDestinations.attendance, inclusive = true)
                    .build()
        }
    }
}

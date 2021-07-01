package org.robojackets.apiary.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.robojackets.apiary.android.R
import org.robojackets.apiary.ui.auth.LoginScreen

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Attendance : Screen("attendance_splash", R.string.attendance)
}

@Composable
fun ApiaryAndroid(attendanceViewModel: AttendanceViewModel = viewModel()) {
    val navController = rememberNavController()

    val loggedIn = remember { mutableStateOf(false) }
//    val viewState by attendanceViewModel.state.collectAsState()

    if (!loggedIn.value) {
        LoginScreen()
    } else {
        val navItems = listOf(
            Screen.Attendance
        )

        // Bottom Nav sample from docs basically: https://developer.android.com/jetpack/compose/navigation?hl=ca#bottom-nav
        Scaffold(
            bottomBar = {
                BottomNavigation {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    navItems.forEach { screen ->
                        BottomNavigationItem(
                            icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                            label = { Text(stringResource(screen.resourceId)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Attendance.route,
                Modifier.padding(innerPadding)
            ) {
                composable(Screen.Attendance.route) {
                    Text("Attendance!")
                }
            }
        }
    }
}

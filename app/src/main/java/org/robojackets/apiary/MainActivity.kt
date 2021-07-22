package org.robojackets.apiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.robojackets.apiary.android.R
import org.robojackets.apiary.attendance.AttendanceScreen
import org.robojackets.apiary.auth.AuthenticationScreen
import org.robojackets.apiary.base.ui.theme.Apiary_MobileTheme
import org.robojackets.apiary.navigation.NavigationDirections
import org.robojackets.apiary.navigation.NavigationManager
import javax.inject.Inject

sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector, val imgContentDescriptor: String) {
    object Attendance : Screen(NavigationDirections.Attendance.destination, R.string.attendance, Icons.Filled.Search, "search")
    object Settings : Screen(NavigationDirections.Authentication.destination, R.string.settings, Icons.Filled.Settings, "settings")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var application: ApiaryMobileApplication

    // Based on https://stackoverflow.com/a/66838316
    @Composable
    fun currentRoute(navController: NavHostController): String? {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        return navBackStackEntry?.destination?.route
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navItems = listOf(
            Screen.Attendance,
            Screen.Settings,
        )

        setContent {
            Apiary_MobileTheme {
                window.statusBarColor = MaterialTheme.colors.primaryVariant.toArgb()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        bottomBar = {
                            if (currentRoute(navController) != NavigationDirections.Authentication.destination) {
                                BottomNavigation {
                                    navItems.forEach { screen ->
                                        BottomNavigationItem(
                                            icon = {
                                                Icon(
                                                    screen.icon,
                                                    contentDescription = screen.imgContentDescriptor
                                                )
                                            },
                                            label = { Text(stringResource(screen.resourceId)) },
                                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                            onClick = {
                                                // TODO
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
                        }
                    ) { innerPadding ->
                        AppNavigation(navController)
                    }
                }
            }
        }
    }

    @Composable
    private fun AppNavigation(navController: NavHostController) {
        NavHost(
            navController = navController,
            startDestination = NavigationDirections.Authentication.destination
        ) {
            composable(NavigationDirections.Authentication.destination) {
                AuthenticationScreen(hiltViewModel())
            }
            composable(NavigationDirections.Attendance.destination) {
                AttendanceScreen(hiltViewModel())
            }
        }
        navigationManager.commands.collectAsState().value.also { command ->
            if (command?.destination?.isNotEmpty() == true) {
                navController.navigate(command.destination)
            }
        }
    }
}
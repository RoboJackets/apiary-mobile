package org.robojackets.apiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.toArgb
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.robojackets.apiary.attendance.AttendanceScreen
import org.robojackets.apiary.auth.AuthenticationScreen
import org.robojackets.apiary.navigation.NavigationDirections
import org.robojackets.apiary.navigation.NavigationManager
import org.robojackets.apiary.ui.theme.Apiary_MobileTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigationManager: NavigationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Apiary_MobileTheme {
                window.statusBarColor = MaterialTheme.colors.primaryVariant.toArgb()
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    navigationManager.commands.collectAsState().value.also { command ->
                        if (command.destination.isNotEmpty()) {
                            navController.navigate(command.destination)
                        }
                    }
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
                }
            }
        }
    }
}
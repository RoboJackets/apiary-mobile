package org.robojackets.apiary

import android.content.Context
import android.content.Intent
import android.nfc.NfcManager
import android.os.Bundle
import android.provider.Settings.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Contactless
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.nxp.nfclib.NxpNfcLib
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.robojackets.apiary.attendance.AttendanceScreen
import org.robojackets.apiary.attendance.ui.AttendableSelectionScreen
import org.robojackets.apiary.attendance.ui.AttendableTypeSelectionScreen
import org.robojackets.apiary.auth.AuthStateManager
import org.robojackets.apiary.auth.AuthenticationScreen
import org.robojackets.apiary.auth.oauth2.AuthManager
import org.robojackets.apiary.base.GlobalSettings
import org.robojackets.apiary.base.model.AttendableType
import org.robojackets.apiary.base.ui.ActionPrompt
import org.robojackets.apiary.base.ui.IconWithText
import org.robojackets.apiary.base.ui.icons.ErrorIcon
import org.robojackets.apiary.base.ui.icons.WarningIcon
import org.robojackets.apiary.base.ui.theme.Apiary_MobileTheme
import org.robojackets.apiary.base.ui.theme.danger
import org.robojackets.apiary.base.ui.theme.warning
import org.robojackets.apiary.navigation.NavigationActions
import org.robojackets.apiary.navigation.NavigationDestinations
import org.robojackets.apiary.navigation.NavigationManager
import org.robojackets.apiary.ui.settings.SettingsScreen
import timber.log.Timber
import javax.inject.Inject

sealed class Screen(
    val navigationDestination: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector,
    val imgContentDescriptor: String
) {
    object Attendance :
        Screen(
            NavigationDestinations.attendanceSubgraph,
            R.string.attendance,
            Icons.Outlined.Contactless,
            "contactless"
        )

    object Settings :
        Screen(
            NavigationDestinations.settings,
            R.string.settings,
            Icons.Filled.Settings,
            "settings"
        )
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var application: ApiaryMobileApplication

    @Inject
    lateinit var authManager: AuthManager

    @Inject
    lateinit var settings: GlobalSettings

    @Inject
    lateinit var authStateManager: AuthStateManager

    @Inject
    lateinit var nfcLib: NxpNfcLib

    // Based on https://stackoverflow.com/a/66838316
    @Composable
    fun currentRoute(navController: NavHostController): String? {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        return navBackStackEntry?.destination?.route
    }

    private fun initMifareLib() {
        nfcLib = NxpNfcLib.getInstance()

        nfcLib.registerActivity(this, BuildConfig.taplinxKey, BuildConfig.taplinxOfflineKey)
    }

    @ExperimentalMaterialApi
    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navItems = listOf(
            Screen.Attendance,
            Screen.Settings,
        )

        // Based on https://stackoverflow.com/a/23564553
        val nfcManager = applicationContext.getSystemService(Context.NFC_SERVICE) as NfcManager
        val nfcAdapter = nfcManager.defaultAdapter
        val nfcEnabled = nfcAdapter?.isEnabled == true

        if (nfcEnabled) {
            initMifareLib()
        }

        setContent {
            val context = LocalContext.current
            Apiary_MobileTheme {
                window.statusBarColor = MaterialTheme.colors.primaryVariant.toArgb()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // Based on https://medium.com/@Syex/jetpack-compose-navigation-architecture-with-viewmodels-1de467f19e1c
                LaunchedEffect("navigation") {
                    navigationManager.sharedFlow.onEach {
                        Timber.d("Nav command to " + it.destination)
                        navController.navigate(it.destination, it.navOptions)
                    }.launchIn(this)
                }

                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        topBar = {
                            Column {
                                TopAppBar(
                                    title = {
                                        Text(
                                            text = "MyRoboJackets",
                                            style = MaterialTheme.typography.h5,
                                            fontWeight = FontWeight.W800
                                        )
                                    },
                                )

                                if (!settings.appEnv.production) {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colors.error)
                                            .align(CenterHorizontally)
                                            .padding(vertical = 4.dp)
                                    ) {
                                        IconWithText(
                                            icon = { WarningIcon(tint = Color.White) },
                                            text = { Text(
                                                "Non-production server",
                                                modifier = Modifier.padding(start = 4.dp),
                                                color = Color.White
                                            ) }
                                        )
                                    }
                                }
                            }
                        },
                        bottomBar = {
                            val current = currentRoute(navController)
                            if (nfcEnabled && current != NavigationDestinations.authentication) {
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
                                            selected = currentDestination
                                                ?.hierarchy
                                                ?.any {
                                                    it.route == screen.navigationDestination
                                                } == true,
                                            onClick = {
                                                navigationManager.navigate(
                                                    NavigationActions.BottomNavTabs.withinBottomNavTabs(
                                                        screen.navigationDestination,
                                                        navController.graph.findStartDestination().id
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        var enableNfcClicked by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.padding(innerPadding)) {
                            if (nfcEnabled) {
                                AppNavigation(navController)
                            } else {
                                Column(Modifier.fillMaxHeight(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    when (enableNfcClicked) {
                                        false -> {
                                            ActionPrompt(
                                                icon = { ErrorIcon(Modifier.size(114.dp), tint = danger) },
                                                title = "NFC is disabled",
                                                subtitle = "Please enable NFC and restart the app to continue"
                                            )
                                            Button(onClick = {
                                                // Based on https://stackoverflow.com/a/14989772
                                                context.startActivity(Intent(ACTION_NFC_SETTINGS))
                                                enableNfcClicked = true
                                            }) {
                                                Text("Enable NFC")
                                            }
                                        }
                                        true -> {
                                            ActionPrompt(
                                                icon = { ErrorIcon(Modifier.size(114.dp), tint = warning) },
                                                title = "Restart to continue",
                                                subtitle = "If you've enabled NFC, just restart the app and you'll be on your way!"
                                            )
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @ExperimentalMaterialApi
    @Composable
    private fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
        val startDestination =
            if (!authStateManager.current.isAuthorized) NavigationDestinations.authentication
            else NavigationDestinations.attendanceSubgraph

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier,
        ) {
            composable(NavigationDestinations.authentication) {
                AuthenticationScreen(hiltViewModel(), authManager)
            }

            navigation(
                startDestination = NavigationDestinations.attendableTypeSelection,
                route = NavigationDestinations.attendanceSubgraph
            ) {
                composable(NavigationDestinations.attendableTypeSelection) {
                    AttendableTypeSelectionScreen(hiltViewModel())
                }

                composable(
                    route = "${NavigationDestinations.attendableSelection}/{attendableType}",
                    arguments = listOf(
                        navArgument("attendableType") { type = NavType.StringType }
                    ),
                ) {
                    val attendableType = it.arguments?.get("attendableType")

                    AttendableSelectionScreen(
                        hiltViewModel(),
                        AttendableType.valueOf(attendableType as String)
                    )
                }

                composable(
                    route = "${NavigationDestinations.attendance}/{attendableType}/{attendableId}",
                    arguments = listOf(
                        navArgument("attendableType") { type = NavType.StringType },
                        navArgument("attendableId") { type = NavType.IntType },
                    )
                ) {
                    val attendableType = it.arguments?.get("attendableType")
                    val attendableId = it.arguments?.get("attendableId")

                    AttendanceScreen(
                        hiltViewModel(), nfcLib, AttendableType.valueOf(attendableType as String),
                        attendableId as Int
                    )
                }
            }
            composable(NavigationDestinations.settings) {
                SettingsScreen(hiltViewModel())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
        // The AppAuth AuthenticationService has to be properly cleaned up to avoid
        // crashes. This `dispose` call works alongside Hilt, which destroys the single AuthManager
        // instance when this Activity is destroyed.
        authManager.authService.dispose()

        // Clear the last navigation command when exiting the app
//        navigationManager.commands.value = null
    }
}

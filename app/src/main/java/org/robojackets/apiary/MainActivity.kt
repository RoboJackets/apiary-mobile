package org.robojackets.apiary

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
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
import com.nxp.nfclib.NxpNfcLib
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import org.robojackets.apiary.attendance.AttendanceScreen
import org.robojackets.apiary.auth.AuthenticationScreen
import org.robojackets.apiary.auth.oauth2.AuthManager
import org.robojackets.apiary.base.CardScanEvent
import org.robojackets.apiary.base.GlobalSettings
import org.robojackets.apiary.base.ui.theme.Apiary_MobileTheme
import org.robojackets.apiary.base.ui.theme.SettingsScreen
import org.robojackets.apiary.navigation.NavigationCommand
import org.robojackets.apiary.navigation.NavigationDirections
import org.robojackets.apiary.navigation.NavigationManager
import javax.inject.Inject


sealed class Screen(
    val navigationCommand: NavigationCommand,
    @StringRes val resourceId: Int,
    val icon: ImageVector,
    val imgContentDescriptor: String
) {
    object Attendance :
        Screen(NavigationDirections.Attendance, R.string.attendance, Icons.Filled.Search, "search")

    object Settings :
        Screen(NavigationDirections.Settings, R.string.settings, Icons.Filled.Settings, "settings")
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

    private var nfcLib: NxpNfcLib? = null


    private val _cardScanFlow = MutableStateFlow<CardScanEvent?>(null)
    private val cardScanFlow: StateFlow<CardScanEvent?>
        get() = _cardScanFlow

//    lateinit var taplinxInstance: Nxp
    // Based on https://stackoverflow.com/a/66838316
    @Composable
    fun currentRoute(navController: NavHostController): String? {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        return navBackStackEntry?.destination?.route
    }

    private fun initMifareLib() {
        nfcLib = NxpNfcLib.getInstance()

        nfcLib?.registerActivity(this, BuildConfig.taplinxKey, BuildConfig.taplinxOfflineKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navItems = listOf(
            Screen.Attendance,
            Screen.Settings,
        )

        initMifareLib()


        setContent {
            Apiary_MobileTheme {
                window.statusBarColor = MaterialTheme.colors.primaryVariant.toArgb()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val navState = navigationManager.commands.collectAsState()
                LaunchedEffect(navState) {
                    snapshotFlow { navState.value }
                        .distinctUntilChanged()
                        .filterNotNull()
                        .collect(handleNavigationCommand(navController))
                }

                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        bottomBar = {
                            if (currentRoute(navController) != NavigationDirections.Authentication
                                    .destination) {
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
                                                    it.route == screen.navigationCommand.destination
                                                } == true,
                                            onClick = {
                                                navigationManager.navigate(screen.navigationCommand)
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

    private fun handleNavigationCommand(navController: NavHostController):
            suspend (value: NavigationCommand) -> Unit =
        { command ->
            if (command.destination.isNotEmpty()) {
                navController.navigate(command.destination) {
                    if (command.isInBottomNav) {
                        if (navController.graph.findStartDestination().route ==
                            NavigationDirections.Authentication.destination) {
                            // Clear anything before and including the Authentication screen to
                            // remove the login flow from the back stack
                            popUpTo(route = NavigationDirections.Authentication.destination) {
                                inclusive = true
                            }
                        } else {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }

                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    } else if (navController.graph.findStartDestination().route ==
                        NavigationDirections.Authentication.destination) {
                        // Clear anything before and including the Authentication screen to
                        // remove the login flow from the back stack
                        popUpTo(route = NavigationDirections.Authentication.destination) {
                            inclusive = true
                        }
                    }
                }
            }
        }

    @Composable
    private fun AppNavigation(navController: NavHostController) {
        val startDestination = if (settings.accessToken.isBlank()) NavigationDirections.Authentication.destination
            else NavigationDirections.Attendance.destination

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(NavigationDirections.Authentication.destination) {
                AuthenticationScreen(hiltViewModel(), authManager)
            }
            composable(NavigationDirections.Attendance.destination) {
                AttendanceScreen(hiltViewModel(), cardScanFlow, nfcLib)
            }
            composable(NavigationDirections.Settings.destination) {
                SettingsScreen()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
//        Log.d(TAG, "---------")
//        Log.d(TAG, "onNewIntent")
//        Log.d(TAG, "Card type: ${nfcLib?.getCardType(intent)}")
//
//        val desFireEV1 = DESFireFactory.getInstance().getDESFire(nfcLib?.customModules)
//        val buzz_application = 0xBBBBCD
//        val buzz_file = 0x01
//        var buzzData = ByteArray(48)
//        var buzzString: String? = null
//
//        desFireEV1.selectApplication(buzz_application)
//        buzzData = desFireEV1.readData(buzz_file, 0, buzzData.size)
//        buzzString = String(buzzData, StandardCharsets.UTF_8)
//        val buzzStringParts = buzzString.split("=").toTypedArray()
//        val gtid = buzzStringParts[0]
//
//        Log.d(TAG, "gtid: $gtid")
//
//        _cardScanFlow.value = CardScanEvent(gtid)
    }

    override fun onResume() {
        super.onResume()
        nfcLib?.startForeGroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        nfcLib?.stopForeGroundDispatch()
    }

    override fun onDestroy() {
        super.onDestroy()
        // The AppAuth AuthenticationService has to be properly cleaned up to avoid
        // crashes. This `dispose` call works alongside Hilt, which destroys the single AuthManager
        // instance when this Activity is destroyed.
        authManager.authService.dispose()
    }
}

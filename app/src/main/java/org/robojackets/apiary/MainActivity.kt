package org.robojackets.apiary

import android.content.Context
import android.nfc.NfcManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Contactless
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.nxp.nfclib.NxpNfcLib
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.robojackets.apiary.attendance.AttendanceScreen
import org.robojackets.apiary.attendance.ui.AttendableSelectionScreen
import org.robojackets.apiary.attendance.ui.AttendableTypeSelectionScreen
import org.robojackets.apiary.auth.AuthStateManager
import org.robojackets.apiary.auth.oauth2.AuthManager
import org.robojackets.apiary.auth.ui.AuthenticationScreen
import org.robojackets.apiary.base.GlobalSettings
import org.robojackets.apiary.base.model.AttendableType
import org.robojackets.apiary.base.ui.nfc.NfcRequired
import org.robojackets.apiary.base.ui.theme.Apiary_MobileTheme
import org.robojackets.apiary.navigation.NavigationActions
import org.robojackets.apiary.navigation.NavigationDestinations
import org.robojackets.apiary.navigation.NavigationManager
import org.robojackets.apiary.ui.global.AppTopBar
import org.robojackets.apiary.ui.settings.SettingsScreen
import org.robojackets.apiary.ui.update.OptionalUpdatePrompt
import org.robojackets.apiary.ui.update.RequiredUpdatePrompt
import org.robojackets.apiary.ui.update.UpdateGate
import org.robojackets.apiary.ui.update.UpdateInProgress
import timber.log.Timber
import javax.inject.Inject

// TODO: see if we can make app launch screen match light/dark mode?

sealed class Screen(
    val navigationDestination: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector,
    val imgContentDescriptor: String,
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

    @OptIn(ExperimentalMaterialNavigationApi::class)
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
            Apiary_MobileTheme {
                window.statusBarColor = MaterialTheme.colorScheme.secondary.toArgb()
                val navController = rememberNavController()
                val bottomSheetNavigator = rememberBottomSheetNavigator()
                navController.navigatorProvider += bottomSheetNavigator
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                var navReady by remember { mutableStateOf(false) }
                // Based on https://medium.com/@Syex/jetpack-compose-navigation-architecture-with-viewmodels-1de467f19e1c
                LaunchedEffect("navigation") {
                    navigationManager.sharedFlow.onEach {
                        Timber.d("Nav command to " + it.destination)
                        navController.navigate(it.destination, it.navOptions)
                    }.launchIn(this)
                    navReady = true
                }

                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    ModalBottomSheetLayout(bottomSheetNavigator) {
                        UpdateGate(
                            navReady = navReady,
                            onShowRequiredUpdatePrompt = {
                                navigationManager.navigate(
                                    NavigationActions.UpdatePrompts.anyScreenToRequiredUpdatePrompt()
                                )
                            },
                            onShowOptionalUpdatePrompt = {
                                navigationManager.navigate(
                                    NavigationActions.UpdatePrompts.anyScreenToOptionalUpdatePrompt()
                                )
                            },
                            onShowUpdateInProgressScreen = {
                                navigationManager.navigate(
                                    NavigationActions.UpdatePrompts.anyScreenToUpdateInProgress()
                                )
                            }
                        ) {
                            Scaffold(
                                topBar = { AppTopBar(settings.appEnv.production) },
                                bottomBar = {
                                    val current = currentRoute(navController)
                                    if (shouldShowBottomNav(nfcEnabled, current)) {
                                        NavigationBar {
                                            navItems.forEach { screen ->
                                                NavigationBarItem(
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
                                Box(modifier = Modifier.padding(innerPadding)) {
                                    NfcRequired(nfcEnabled = nfcEnabled) {
                                        AppNavigation(navController)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun shouldShowBottomNav(
        nfcEnabled: Boolean,
        currentScreen: String?,
    ) = nfcEnabled &&
            currentScreen != NavigationDestinations.authentication &&
            currentScreen != NavigationDestinations.requiredUpdatePrompt &&
            currentScreen != NavigationDestinations.updateInProgress

    @Suppress("LongMethod")
    @OptIn(ExperimentalMaterialNavigationApi::class)
    @ExperimentalMaterialApi
    @Composable
    private fun AppNavigation(
        navController: NavHostController,
        modifier: Modifier = Modifier,
    ) {
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
                        hiltViewModel(),
                        nfcLib,
                        AttendableType.valueOf(attendableType as String),
                        attendableId as Int
                    )
                }
            }
            composable(NavigationDestinations.settings) {
                SettingsScreen(hiltViewModel())
            }
            composable(NavigationDestinations.requiredUpdatePrompt) {
                RequiredUpdatePrompt()
            }
            composable(NavigationDestinations.updateInProgress) {
                UpdateInProgress()
            }
            bottomSheet(NavigationDestinations.optionalUpdatePrompt) {
                OptionalUpdatePrompt(onIgnoreUpdate = {
                    navController.popBackStack()
                })
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
    }
}

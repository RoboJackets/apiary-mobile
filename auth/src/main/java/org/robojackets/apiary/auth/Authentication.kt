package org.robojackets.apiary.auth

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import org.robojackets.apiary.auth.model.AuthenticationState
import org.robojackets.apiary.auth.model.AuthenticationViewModel
import org.robojackets.apiary.auth.model.LoginStatus.*
import org.robojackets.apiary.auth.oauth2.AuthManager
import org.robojackets.apiary.base.AppEnvironment
import org.robojackets.apiary.base.ui.theme.BottomSheetShape
import org.robojackets.apiary.base.ui.util.MadeWithLove

@OptIn(ExperimentalMaterialApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Suppress("LongMethod", "MagicNumber")
@Composable
private fun Authentication(
    viewState: AuthenticationState,
    authManager: AuthManager,
    onAppEnvChange: (newEnv: AppEnvironment) -> Unit,
    viewModel: AuthenticationViewModel,
) {
    // You have to `remember` two things here for some reason
    // In any case, thanks to https://proandroiddev.com/getting-your-bottomsheetscaffold-working-on-jetpack-compose-beta-03-aa829b0c9b6c
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = BottomSheetValue.Collapsed
        )
    )
    val coroutineScope = rememberCoroutineScope()

    val systemUiController = rememberSystemUiController()
    val backgroundColor = MaterialTheme.colors.background
    SideEffect {
        systemUiController.setSystemBarsColor(backgroundColor)
    }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {

                val authResponse = AuthorizationResponse.fromIntent(it.data!!)
                val authException = AuthorizationException.fromIntent(it.data!!)

                when {
                    authResponse != null -> {
                        authManager.authService.performTokenRequest(
                            authResponse.createTokenExchangeRequest()
                        ) { response, ex ->
                            when {
                                response != null -> {
                                    val accessToken = response.accessToken
                                    val refreshToken = response.refreshToken
                                    if (!viewModel.validateAuthInfo(
                                            accessToken,
                                            refreshToken
                                        )
                                    ) {
                                        viewModel.recordAuthError("Unable to validate authentication credentials.")
                                    } else {
                                        viewModel.setLoginStatus(COMPLETE)
                                        viewModel.saveAuthInfo(
                                            response.accessToken!!,
                                            response.refreshToken!!
                                        )
                                        viewModel.navigateToAttendance()
                                    }
                                }
                                ex != null -> viewModel.recordAuthError(ex)
                                else -> viewModel.recordAuthError(null)
                            }
                        }
                    }
                    authException != null -> viewModel.recordAuthError(authException)
                }
            } else {
                viewModel.recordAuthError("The authentication attempt was cancelled.")
            }
        }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetShape = BottomSheetShape,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                content = {
                    ChangeEnvironmentBottomSheetContent(
                        viewState,
                        onAppEnvChange,
                        scaffoldState
                    )
                }
            )
        }) {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_robobuzz_white_outline),
                    contentDescription = "RoboJackets logo",
                    modifier = Modifier
                        .fillMaxWidth(.45f)
                        .weight(1.0f)
                )
                Column(
                    modifier = Modifier.weight(.5f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {

                    Button(
                        onClick = {
                            val authRequest = authManager.getAuthRequest()
                            launcher.launch(
                                authManager.authService.getAuthorizationRequestIntent(
                                    authRequest
                                )
                            )
                        },
                    ) {
                        Text("Sign in with MyRoboJackets")
                    }
                }

                if (viewState.loginStatus == ERROR) {
                    AlertDialog(
                        onDismissRequest = {
                            viewModel.setLoginStatus(NOT_STARTED)
                        },
                        confirmButton = {
                            TextButton(onClick = { viewModel.setLoginStatus(NOT_STARTED) }) {
                                Text("Close")
                            }
                        },
                        title = { Text("Login failed") },
                        text = {
                            Text(
                                "${viewState.loginErrorMessage}\n\nTry logging in again. " +
                                        "If that does not work, please post in #it-helpdesk in Slack."
                            )
                        },
                    )
                }

                TextButton(onClick = {
                    coroutineScope.launch {
                        scaffoldState.bottomSheetState.expand()
                    }
                }) {
                    Text("Change server")
                }
                Text("Server: ${viewState.appEnv.name} (${viewState.appEnv.apiBaseUrl})")
                MadeWithLove()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ChangeEnvironmentBottomSheetContent(
    viewState: AuthenticationState,
    onAppEnvChange: (newEnv: AppEnvironment) -> Unit,
    scaffoldState: BottomSheetScaffoldState
) {
    val coroutineScope = rememberCoroutineScope()
    var unsavedAppEnvSelection by remember { mutableStateOf(viewState.appEnv) }
    val appEnvChoices = AppEnvironment.values()
    val saveNewAppEnvChoice: (() -> Unit) = {
        onAppEnvChange(unsavedAppEnvSelection)
        coroutineScope.launch {
            scaffoldState.bottomSheetState.collapse()
        }
    }

    Text(
        "Change server",
        modifier = Modifier
            .padding(16.dp),
        style = MaterialTheme.typography.h5
    )

    Column(Modifier.selectableGroup()) {
        appEnvChoices.forEach {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (it == unsavedAppEnvSelection),
                        onClick = { unsavedAppEnvSelection = it },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (it == unsavedAppEnvSelection),
                    onClick = null,
                )
                Text(
                    text = "${it.name} (${it.apiBaseUrl})",
                    style = MaterialTheme.typography.body1.merge(),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }

    Button(
        modifier = Modifier.padding(16.dp),
        onClick = saveNewAppEnvChoice,
    ) {
        Text("Save changes")
    }
}

@Composable
fun AuthenticationScreen(
    viewModel: AuthenticationViewModel,
    authManager: AuthManager
) {
    val viewState by viewModel.state.collectAsState()
    Authentication(
        viewState,
        authManager,
        onAppEnvChange = { viewModel.setAppEnv(it.name) },
        viewModel,
    )
}

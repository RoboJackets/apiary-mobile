package org.robojackets.apiary.auth.ui

import android.app.Activity.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import org.robojackets.apiary.auth.R
import org.robojackets.apiary.auth.model.AuthenticationState
import org.robojackets.apiary.auth.model.AuthenticationViewModel
import org.robojackets.apiary.auth.model.LoginStatus.*
import org.robojackets.apiary.auth.oauth2.AuthManager
import org.robojackets.apiary.base.AppEnvironment
import org.robojackets.apiary.base.ui.util.MadeWithLove

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod", "MagicNumber")
@Composable
private fun Authentication(
    viewState: AuthenticationState,
    authManager: AuthManager,
    onAppEnvChange: (newEnv: AppEnvironment) -> Unit,
    viewModel: AuthenticationViewModel,
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    val systemUiController = rememberSystemUiController()
    val backgroundColor = MaterialTheme.colorScheme.background
    SideEffect {
        systemUiController.setSystemBarsColor(backgroundColor)
    }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val authResponse = AuthorizationResponse.fromIntent(it.data!!)
                val authException = AuthorizationException.fromIntent(it.data!!)
                viewModel.updateAuthStateAfterAuthorization(authResponse, authException)
                when {
                    authResponse != null -> {
                        authManager.authService.performTokenRequest(
                            authResponse.createTokenExchangeRequest()
                        ) { response, ex ->
                            viewModel.updateAuthStateAfterTokenResponse(response, ex)
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

    var showChangeEnvBottomSheet by remember { mutableStateOf(false) }


    if (showChangeEnvBottomSheet) {
        ChangeEnvBottomSheet(
            onDismiss = { showChangeEnvBottomSheet = false },
            viewState = viewState,
            onAppEnvChange = {
                showChangeEnvBottomSheet = false
                onAppEnvChange(it)
            }
        )
    }

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
            showChangeEnvBottomSheet = true
        }) {
            Text("Change server")
        }
        Text("Server: ${viewState.appEnv.name} (${viewState.appEnv.apiBaseUrl})")
        MadeWithLove()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeEnvBottomSheet(
    onDismiss: () -> Unit,
    viewState: AuthenticationState,
    onAppEnvChange: (newEnv: AppEnvironment) -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
    ) {
        Column(
            Modifier
                .padding(bottom = 40.dp)
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            content = {
                ChangeEnvironmentBottomSheetContent(
                    viewState,
                    onAppEnvChange,
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangeEnvironmentBottomSheetContent(
    viewState: AuthenticationState,
    onAppEnvChange: (newEnv: AppEnvironment) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var unsavedAppEnvSelection by remember { mutableStateOf(viewState.appEnv) }
    val appEnvChoices = AppEnvironment.values()
    val saveNewAppEnvChoice: (() -> Unit) = {
        onAppEnvChange(unsavedAppEnvSelection)
    }

    Text(
        "Change server",
        modifier = Modifier
            .padding(16.dp),
        style = MaterialTheme.typography.headlineSmall
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
                    style = MaterialTheme.typography.bodyLarge.merge(),
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
    authManager: AuthManager,
) {
    val viewState by viewModel.state.collectAsState()
    Authentication(
        viewState,
        authManager,
        onAppEnvChange = { viewModel.setAppEnv(it.name) },
        viewModel,
    )
}

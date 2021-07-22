package org.robojackets.apiary.auth

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationResponse
import org.robojackets.apiary.auth.model.AuthenticationState
import org.robojackets.apiary.auth.model.AuthenticationViewModel
import org.robojackets.apiary.auth.oauth2.AuthManager
import org.robojackets.apiary.base.AppEnvironment
import org.robojackets.apiary.base.ui.theme.BottomSheetShape

@OptIn(ExperimentalMaterialApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
private fun Authentication(
    viewState: AuthenticationState,
    onAppEnvChange: (newEnv: AppEnvironment) -> Unit,
    viewModel: AuthenticationViewModel,
) {
    val TAG = "Authentication"

    // You have to `remember` two things here for some reason
    // In any case, thanks to https://proandroiddev.com/getting-your-bottomsheetscaffold-working-on-jetpack-compose-beta-03-aa829b0c9b6c
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = BottomSheetValue.Collapsed
        )
    )
    val scope = rememberCoroutineScope()

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            Color.Black
        )
    }

    val context = LocalContext.current

    val loginResult = remember { mutableStateOf<String>("") }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            loginResult.value = "got auth code"
            Log.d(TAG, "Authorization Code obtained, result: $it")
            val authManager = AuthManager(context)
            authManager.authService.performTokenRequest(
                AuthorizationResponse.fromIntent(it.data!!)!!.createTokenExchangeRequest()
            ) { response, ex ->
                Log.d(TAG, "Inside performTokenRequest callback, response: $response, ex: $ex")
                if (response != null) {
                    loginResult.value = response.accessToken?.length.toString()
                    viewModel.navigateToAttendance()
                } else {
                    loginResult.value = ex.toString()
                }
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
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    "Change server",
                    modifier = Modifier
                        .padding(16.dp),
                    style = MaterialTheme.typography.h5
                )

                var unsavedAppEnvSelection by remember { mutableStateOf(viewState.appEnv) }

                val saveNewAppEnvChoice: (() -> Unit) = {
                    onAppEnvChange(unsavedAppEnvSelection)
                    scope.launch {
                        scaffoldState.bottomSheetState.collapse()
                    }
                }

                val appEnvChoices = AppEnvironment.values()

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
    }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Black),
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
                if (viewState.loading) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = {
                            val authManager = AuthManager(context)
                            val authRequest = authManager.authRequest
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

                if (loginResult.value.isNotEmpty()) {
                    Text("A login result is available: ${loginResult.value}")
                }

                if (BuildConfig.DEBUG) {
                    TextButton(onClick = {
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }) {
                        Text("Change server")
                    }
                    Text("Server: ${viewState.appEnv.name} (${viewState.appEnv.apiBaseUrl})")
                }
                Text("Made with ♥ by RoboJackets")
            }
        }
    }
}

@Composable
fun AuthenticationScreen(
    viewModel: AuthenticationViewModel,
) {
    val viewState by viewModel.state.collectAsState()

    Authentication(
        viewState,
        onAppEnvChange = { viewModel.setAppEnv(it.name) },
        viewModel,
    )
}
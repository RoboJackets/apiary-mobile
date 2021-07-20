package org.robojackets.apiary.auth

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationResponse
import org.robojackets.apiary.auth.model.AuthenticationState
import org.robojackets.apiary.auth.model.AuthenticationViewModel
import org.robojackets.apiary.auth.oauth2.AuthManager
import org.robojackets.apiary.base.GlobalSettings
import org.robojackets.apiary.base.ui.theme.BottomSheetShape

@OptIn(ExperimentalMaterialApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
private fun Authentication(
    viewState: AuthenticationState,
    onClick: () -> Unit,
) {
    val TAG = "Authentication"
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed))
    val scope = rememberCoroutineScope()

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            Color.Black
        )
    }

    val context = LocalContext.current
    val settings = GlobalSettings(context)
    val apiBaseUrl = remember { mutableStateOf(settings.apiBaseUrl) }
    val keyboardController = LocalSoftwareKeyboardController.current

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

                val unsavedServerUrl = rememberSaveable { mutableStateOf(apiBaseUrl.value) }

                val saveUpdatedServerUrl: (() -> Unit) = {
                    keyboardController?.hide()
                    apiBaseUrl.value = unsavedServerUrl.value
                    settings.apiBaseUrl = unsavedServerUrl.value
                    scope.launch {
                        scaffoldState.bottomSheetState.collapse()
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.padding(32.dp),
                    value = unsavedServerUrl.value,
                    label = { Text("New server URL") },
                    onValueChange = { unsavedServerUrl.value = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { saveUpdatedServerUrl() }
                    )
                )

                Button(
                    modifier = Modifier.padding(16.dp),
                    onClick = saveUpdatedServerUrl,
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
                    Text("Server: ${apiBaseUrl.value}")
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
        viewState
    ) {
        viewModel.navigateToAttendance()
    }
}
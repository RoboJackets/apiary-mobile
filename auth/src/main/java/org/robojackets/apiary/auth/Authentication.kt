package org.robojackets.apiary.auth

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import net.openid.appauth.AuthorizationResponse
import org.robojackets.apiary.auth.model.AuthenticationState
import org.robojackets.apiary.auth.model.AuthenticationViewModel
import org.robojackets.apiary.auth.oauth2.AuthManager

@Composable
private fun Authentication(
    viewState: AuthenticationState,
    onClick: () -> Unit
) {
    val TAG = "Authentication"
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            Color.Black
        )
    }

    val context = LocalContext.current

    val loginResult = remember { mutableStateOf<String>("") }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        loginResult.value = "got auth code"
        Log.d(TAG, "Authorization Code obtained, result: $it")
        val authManager = AuthManager(context)
        authManager.authService.performTokenRequest(
            AuthorizationResponse.fromIntent(it.data!!)!!.createTokenExchangeRequest()
        ) { response, ex ->
//            viewState.loading = false
            Log.d(TAG, "Inside performTokenRequest callback, response: $response, ex: $ex")
            if (response != null) {
                loginResult.value = response.accessToken?.length.toString()
            } else {
                loginResult.value = ex.toString()
            }
        }
    }

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
        Column(modifier = Modifier.weight(.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            if (viewState.loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
//                        viewState.loading = true
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
                TextButton(onClick = { /*TODO*/ }) {
                    Text("Change server URL")
                }
                Text("Server URL: https://my.robojackets.org")
            }
            Text("Made with ♥ by RoboJackets - vTODO${if (BuildConfig.DEBUG) "-debug" else ""}")
        }
    }
}

@Composable
fun AuthenticationScreen(
    viewModel: AuthenticationViewModel,
) {
    val state by viewModel.state.collectAsState()
    Authentication(state) {
        viewModel.navigateToAttendance()
    }
}
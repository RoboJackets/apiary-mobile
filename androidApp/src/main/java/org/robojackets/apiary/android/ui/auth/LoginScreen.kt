package org.robojackets.apiary.android.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.robojackets.apiary.android.BuildConfig
import org.robojackets.apiary.android.R

@Composable
fun LoginScreen() {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            Color.Black
        )
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
            Button(
                onClick = { /*TODO*/ },
                ) {
                Text("Sign in with MyRoboJackets")
            }

            if (false && BuildConfig.DEBUG) {
                TextButton(onClick = { /*TODO*/ }) {
                    Text("Change server URL")
                }
                Text("Server URL: https://my.robojackets.org")
            }
            Text("Made with ♥ by RoboJackets - v${BuildConfig.VERSION_NAME}${if (BuildConfig.DEBUG) "-debug" else ""}")
        }
    }

//    Column(
//        verticalArrangement = Arrangement.Bottom
//    ) {
//
//    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
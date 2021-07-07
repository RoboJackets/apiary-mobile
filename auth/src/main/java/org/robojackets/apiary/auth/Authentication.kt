package org.robojackets.apiary.auth

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.robojackets.apiary.auth.model.AuthenticationState
import org.robojackets.apiary.auth.model.AuthenticationViewModel

@Composable
private fun Authentication(
    viewState: AuthenticationState,
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Text(text = "Press me")
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
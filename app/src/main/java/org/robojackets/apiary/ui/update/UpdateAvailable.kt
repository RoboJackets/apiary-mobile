package org.robojackets.apiary.ui.update

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.robojackets.apiary.base.ui.icons.UpdateIcon
import se.warting.inappupdate.compose.InAppUpdateState
import se.warting.inappupdate.compose.Mode
import se.warting.inappupdate.compose.rememberInAppUpdateState
import timber.log.Timber

@Suppress("LongMethod")
@Composable
fun InstallUpdateButton(onIgnoreUpdate: () -> Unit = {}) {
    val inAppUpdateState = rememberInAppUpdateState(
        highPrioritizeUpdates = 5,
        mediumPrioritizeUpdates = 3,
        promptIntervalHighPrioritizeUpdateInDays = 1,
        promptIntervalMediumPrioritizeUpdateInDays = 4,
        promptIntervalLowPrioritizeUpdateInDays = 8,
    )
    var updateCanceled by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var updateError by remember { mutableStateOf<String?>(null) }

    when (updateCanceled) {
        true -> {
            Text("Update canceled. Restart the app to try again.", textAlign = TextAlign.Center)
        }
        false -> {
            Button(onClick = {
                when (inAppUpdateState) {
                    is InAppUpdateState.DownloadedUpdate -> {
                        scope.launch {
                            inAppUpdateState.appUpdateResult.completeUpdate()
                        }
                    }

                    is InAppUpdateState.RequiredUpdate -> {
                        inAppUpdateState.onStartUpdate()
                    }

                    is InAppUpdateState.OptionalUpdate -> {
                        inAppUpdateState.onStartUpdate(Mode.IMMEDIATE)
                    }

                    is InAppUpdateState.InProgressUpdate -> {
                        Timber.w("UpdateAvailable: inAppUpdateState is InProgressUpdate")
                        updateError = "The update can't be started because an update is already in" +
                                " progress"
                    }

                    is InAppUpdateState.Error -> {
                        Timber.e(
                            "UpdateAvailable: inAppUpdateState is Error: ${inAppUpdateState.exception}"
                        )
                        updateError =
                            "An update is available, but an error occurred while trying to start" +
                                    " it."
                    }
                    else -> {
                        updateError = "The update can't be started right now"
                    }
                }
                if (updateError?.isNotBlank() == true) {
                    Timber.e("Update error: $updateError")
                    updateCanceled = true
                }
            }) {
                Text("Update now")
            }
        }
    }

    if (updateError?.isNotBlank() == true) {
        AlertDialog(
            onDismissRequest = {
                updateError = null
                onIgnoreUpdate()
                               },
            confirmButton = {
                TextButton(onClick = { updateError = null }) {
                    Text("Close")
                }
            },
            title = { Text("Update failed") },
            text = {
                Text(
                    "${
                    updateError ?: (
                        "An unknown error occurred while starting the " +
                            "update."
                    )
                }\n\nPlease try again, or post in #it-helpdesk for assistance."
                )
            }
        )
    }
}

@Suppress("MagicNumber")
@Composable
fun RequiredUpdatePrompt() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight(),
    ) {
        UpdateIcon(
            Modifier
                .padding(bottom = 18.dp)
                .size(96.dp)
        )
        Text("Update to continue", style = MaterialTheme.typography.headlineMedium)
        Text(
            "To continue using MyRoboJackets, install the latest version. It'll only take a minute.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp)
            )
        InstallUpdateButton()
        Spacer(Modifier.fillMaxHeight(0.55F))
    }
}

@Suppress("MagicNumber")
@Composable
fun OptionalUpdatePrompt(
    onIgnoreUpdate: () -> Unit
) {
    val inAppUpdateState = rememberInAppUpdateState(
        highPrioritizeUpdates = 5,
        mediumPrioritizeUpdates = 3,
        promptIntervalHighPrioritizeUpdateInDays = 1,
        promptIntervalMediumPrioritizeUpdateInDays = 4,
        promptIntervalLowPrioritizeUpdateInDays = 8,
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight(.5F),
    ) {
        UpdateIcon(
            Modifier
                .padding(bottom = 9.dp)
                .size(72.dp)
        )
        Text("Update available", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Install the latest version of MyRoboJackets for the latest features and " +
                "bug fixes. It'll only take a minute.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(20.dp)
        )
        InstallUpdateButton(onIgnoreUpdate)
        TextButton(onClick = {
            if (inAppUpdateState is InAppUpdateState.OptionalUpdate) {
                inAppUpdateState.onDeclineUpdate()
            }
            onIgnoreUpdate()
        }) {
            Text("Not now")
        }
    }
}

@Suppress("MagicNumber")
@Preview
@Composable
fun UpdateInProgress() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight(),
    ) {
        CircularProgressIndicator(Modifier.padding(bottom = 28.dp))
        Text("Please wait...", style = MaterialTheme.typography.headlineSmall)
        Text(
            "We're finishing installing an update. It'll just be a minute or two!",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(20.dp)
        )
        Spacer(Modifier.fillMaxHeight(0.55F))
    }
}

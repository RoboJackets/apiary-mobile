package org.robojackets.apiary.ui.update

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.play.core.ktx.AppUpdateResult
import org.robojackets.apiary.base.ui.icons.UpdateIcon
import org.robojackets.apiary.base.ui.util.getActivity
import se.warting.inappupdate.compose.rememberInAppUpdateState
import timber.log.Timber

// Just a random number so we can identify our update request later if necessary
const val UPDATE_REQUEST_CODE = 1999

fun triggerImmediateUpdate(appUpdateResult: AppUpdateResult.Available, activity: Activity) {
    appUpdateResult.startImmediateUpdate(activity, UPDATE_REQUEST_CODE)
}

@Composable
fun InstallUpdateButton(onIgnoreUpdate: () -> Unit = {}) {
    val updateState = rememberInAppUpdateState()
    var updateCanceled by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var updateError by remember { mutableStateOf<String?>(null) }

    when (updateCanceled) {
        true -> {
            Text("Update canceled. Restart the app to try again.", textAlign = TextAlign.Center)
        }
        false -> {
            Button(onClick = {
                val result = updateState.appUpdateResult
                if (result is AppUpdateResult.Available) {
                    context.getActivity()?.let { triggerImmediateUpdate(result, it) } ?: run {
                        Timber.e("Context.getActivity() was null while trying to start immediate update")
                        updateError = "An update is available, but we were unable to start the update process."
                    }
                } else {
                    Timber.e("User is in update flow but no update was available")
                    updateError = "Sorry! It seems there are no updates to install."
                }
                updateCanceled = true
            }) {
                Text("Download and install update")
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
                Text("${
                    updateError ?: ("An unknown error occurred while starting the " +
                            "update.")
                }\n\nPlease try again, or post in #it-helpdesk for assistance.")
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
        UpdateIcon(Modifier
            .padding(bottom = 18.dp)
            .size(96.dp))
        Text("Update to continue", style = MaterialTheme.typography.h4)
        Text("To continue using MyRoboJackets, install the latest version. It'll only take a minute.",
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight(.5F),
    ) {
        UpdateIcon(Modifier
            .padding(bottom = 9.dp)
            .size(72.dp))
        Text("Update available", style = MaterialTheme.typography.h5)
        Text("Install the latest version of MyRoboJackets for the latest features and " +
                "bug fixes. It'll only take a minute.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(20.dp)
        )
        InstallUpdateButton(onIgnoreUpdate)
        TextButton(onClick = onIgnoreUpdate) {
            Text("Remind me later")
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
        Text("Please wait...", style = MaterialTheme.typography.h5)
        Text("We're finishing installing an update. It'll just be a minute or two!",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(20.dp)
        )
        Spacer(Modifier.fillMaxHeight(0.55F))
    }
}

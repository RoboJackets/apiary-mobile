package org.robojackets.apiary.base.ui.update

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.play.core.ktx.AppUpdateResult
import org.robojackets.apiary.base.ui.icons.UpdateIcon
import se.warting.inappupdate.compose.rememberInAppUpdateState
import timber.log.Timber

fun triggerImmediateUpdate(appUpdateResult: AppUpdateResult.Available, activity: Activity) {
    val requestCode = 1999
    appUpdateResult.startImmediateUpdate(activity, requestCode)
}

@Composable
fun InstallUpdateButton() {
    val updateState = rememberInAppUpdateState()
    val context = LocalContext.current
    var updateError by remember { mutableStateOf<String?>(null) }

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
    }) {
        Text("Download and install update")
    }
    if (updateError?.isNotBlank() == true) {
        AlertDialog(
            onDismissRequest = { updateError = null },
            confirmButton = {
                TextButton(onClick = { updateError = null }) {
                    Text("Close")
                }
            },
            title = { Text("Update failed")},
            text = {
                Text("${updateError ?: "An unknown error occurred while starting the update."}\n\nPlease try again, or post in #it-helpdesk for assistance.")
            }
        )
    }
}

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
        Text("Install the latest version of MyRoboJackets for the latest features and bug fixes. It'll only take a minute.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(20.dp)
        )
        InstallUpdateButton()
        TextButton(onClick = onIgnoreUpdate) {
            Text("Remind me later")
        }
    }
}

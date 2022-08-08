package org.robojackets.apiary.base.ui.update

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.base.ui.icons.UpdateIcon

@Composable
fun RequiredUpdatePrompt(
    onTriggerUpdate: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight(),
    ) {
        UpdateIcon(Modifier.padding(bottom = 18.dp).size(96.dp))
        Text("Update to continue", style = MaterialTheme.typography.h4)
        Text("To continue using MyRoboJackets, install the latest version. It'll only take a minute.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp)
            )
        Button(onClick = onTriggerUpdate) {
            Text("Download and install update")
        }
        Spacer(Modifier.fillMaxHeight(0.55F))
    }
}

@Composable
fun OptionalUpdatePrompt(
    onTriggerUpdate: () -> Unit,
    onIgnoreUpdate: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight(),
    ) {
        UpdateIcon(Modifier.padding(bottom = 9.dp).size(48.dp))
        Text("Update available", style = MaterialTheme.typography.h5)
        Text("Install the latest version of MyRoboJackets for the latest features and bug fixes. It'll only take a minute.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
        Button(onClick = onTriggerUpdate) {
            Text("Download and install update")
        }
        Button(onClick = onIgnoreUpdate) {
            Text("Remind me later")
        }
    }
}

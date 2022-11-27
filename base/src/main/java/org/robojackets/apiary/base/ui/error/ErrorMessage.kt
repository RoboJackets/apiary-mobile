package org.robojackets.apiary.base.ui.error

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import org.robojackets.apiary.base.ui.icons.ErrorIcon
import org.robojackets.apiary.base.ui.theme.danger

@Composable
fun ErrorMessageWithRetry(
    message: String,
    onRetry: () -> Unit,
    showHelpButton: Boolean = true,
    retryButton: @Composable () -> Unit = {
        OutlinedButton(onClick = onRetry) {
            Text("Retry")
        }
    }
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        ErrorIcon(Modifier.size(90.dp), tint = danger)

        Text(
            text = message,
            modifier = Modifier.padding(top = 12.dp),
            textAlign = TextAlign.Center,
        )

        Row(Modifier.padding(top = 12.dp)) {
            retryButton()
            if (showHelpButton) {
                GoToItHelpdesk(modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
fun GoToItHelpdesk(modifier: Modifier) {
    val context = LocalContext.current

    Button(onClick = {
        val slackDeepLink = "slack://channel?team=T033JPZLT&id=C29Q3D8K0"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(slackDeepLink))
        ContextCompat.startActivity(context, intent, null)
    }, modifier) {
        Text("Go to #it-helpdesk")
    }
}

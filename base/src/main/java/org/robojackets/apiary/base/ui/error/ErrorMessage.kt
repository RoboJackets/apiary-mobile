package org.robojackets.apiary.base.ui.error

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import org.robojackets.apiary.base.ui.icons.ErrorIcon
import org.robojackets.apiary.base.ui.theme.Apiary_MobileTheme
import org.robojackets.apiary.base.ui.theme.danger

@Composable
fun ErrorMessageWithRetry(
    message: String? = null,
    onRetry: () -> Unit,
    title: String? = null,
    prioritizeRetryButton: Boolean = true,
    icon: @Composable () -> Unit = {
        ErrorIcon(Modifier.size(90.dp), tint = danger)
    }
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        icon()
        if (title?.isNotEmpty() == true) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        if (message?.isNotEmpty() == true) {
            Text(
                text = message,
                modifier = Modifier.padding(top = 12.dp),
                textAlign = TextAlign.Center,
            )
        }

        Row(Modifier.padding(top = 12.dp)) {
            if (prioritizeRetryButton) {
                GoToItHelpdesk(
                    modifier = Modifier.padding(end = 8.dp),
                    useOutlinedButton = true
                )
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            } else {
                OutlinedButton(onClick = onRetry) {
                    Text("Retry")
                }
                GoToItHelpdesk(
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun GoToItHelpdesk(modifier: Modifier, useOutlinedButton: Boolean = false) {
    val context = LocalContext.current

    fun onClick() {
        val slackDeepLink = "slack://channel?team=T033JPZLT&id=C29Q3D8K0"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(slackDeepLink))
        ContextCompat.startActivity(context, intent, null)
    }

    val text = @Composable { Text("Go to #it-helpdesk") }

    when {
        useOutlinedButton -> {
            OutlinedButton(onClick = { onClick() }, modifier) {
                text()
            }
        }
        else -> {
            Button(onClick = { onClick() }, modifier) {
                text()
            }
        }
    }
}

@Preview
@Composable
fun ErrorMessageWithRetryPreview() {
    Apiary_MobileTheme {
        ErrorMessageWithRetry(
            title = "Error while checking permissions",
            onRetry = {},
            prioritizeRetryButton = true,
        )
    }
}

@Preview
@Composable
fun ErrorMessageWithRetryPrioritizeHelpPreview() {
    Apiary_MobileTheme {
        ErrorMessageWithRetry(
            title = "Attendance unavailable",
            message = "An error occurred while checking your permissions",
            onRetry = {},
            prioritizeRetryButton = false,
        )
    }
}

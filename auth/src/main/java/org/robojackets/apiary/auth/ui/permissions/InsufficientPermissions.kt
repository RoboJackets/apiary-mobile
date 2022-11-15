package org.robojackets.apiary.auth.ui.permissions

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.auth.model.Permission
import org.robojackets.apiary.auth.model.Permission.*
import org.robojackets.apiary.base.ui.icons.ErrorIcon
import org.robojackets.apiary.base.ui.theme.danger
import org.robojackets.apiary.base.ui.util.ContentPadding

@Composable
fun InsufficientPermissions(
    featureName: String,
    missingPermissions: List<Permission>,
    requiredPermissions: List<Permission>,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        ErrorIcon(Modifier.size(90.dp), tint = danger)
        Text(text = "$featureName unavailable",
            style = MaterialTheme.typography.h4)
        Text(
            text = "You don't have permission to use this feature. Please ask in #it-helpdesk for assistance.",
            modifier = Modifier.padding(top = 12.dp),
            textAlign = TextAlign.Center,
        )

        Row(Modifier.padding(top = 18.dp)) {
            OutlinedButton(onClick = {}) {
                Text("Try again")
            }
            Button(onClick = {}, Modifier.padding(start = 8.dp)) {
                Text("Go to #it-helpdesk")
            }
        }

        TextButton(onClick = {}, Modifier.padding(top = 0.dp)) {
            Text("More info")
        }
    }
}

@Composable
@Preview
fun InsufficientPermissionsPreview() {
    ContentPadding {
        InsufficientPermissions(
            featureName = "Attendance",
            missingPermissions = listOf(READ_TEAMS_HIDDEN),
            requiredPermissions = listOf(CREATE_ATTENDANCE, READ_USERS, READ_TEAMS_HIDDEN)
        )
    }
}
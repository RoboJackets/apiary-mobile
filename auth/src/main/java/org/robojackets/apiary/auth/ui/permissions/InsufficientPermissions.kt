package org.robojackets.apiary.auth.ui.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.auth.model.Permission
import org.robojackets.apiary.auth.model.Permission.CREATE_ATTENDANCE
import org.robojackets.apiary.auth.model.Permission.READ_TEAMS_HIDDEN
import org.robojackets.apiary.auth.model.Permission.READ_USERS
import org.robojackets.apiary.base.ui.error.GoToItHelpdesk
import org.robojackets.apiary.base.ui.icons.ErrorIcon
import org.robojackets.apiary.base.ui.theme.Apiary_MobileTheme
import org.robojackets.apiary.base.ui.theme.danger
import org.robojackets.apiary.base.ui.theme.success
import org.robojackets.apiary.base.ui.util.ContentPadding

@Composable
fun InsufficientPermissions(
    featureName: String,
    onRefreshRequest: () -> Unit,
    missingPermissions: List<Permission>,
    requiredPermissions: List<Permission>,
) {
    var showPermissionDetailsDialog by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        ErrorIcon(Modifier.size(90.dp), tint = danger)
        Text(
            text = "$featureName permissions required",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 12.dp),
        )

        Row(Modifier.padding(top = 18.dp)) {
            OutlinedButton(onClick = { onRefreshRequest() }) {
                Text("Retry")
            }
            GoToItHelpdesk(modifier = Modifier.padding(start = 8.dp))
        }

        TextButton(onClick = {
            showPermissionDetailsDialog = true
        }, Modifier.padding(top = 0.dp)) {
            Text("View details")
        }

        if (showPermissionDetailsDialog) {
            PermissionDetailsDialog(
                onHide = {
                    showPermissionDetailsDialog = false
                },
                missingPermissions = missingPermissions,
                satisfiedPermissions = requiredPermissions.subtract(missingPermissions.toSet())
                    .toList()
            )
        }
    }
}

@Composable
fun PermissionDetailsDialog(
    onHide: () -> Unit,
    missingPermissions: List<Permission>,
    satisfiedPermissions: List<Permission>,
) {
    AlertDialog(
        onDismissRequest = onHide,
        text = {
            Column(
                Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Required permissions",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Divider()
                LazyColumn {
                    items(items = missingPermissions) { permission ->
                        PermissionsListItem(
                            hasPermission = false,
                            permissionName = permission.toString()
                        )
                        Divider()
                    }

                    items(items = satisfiedPermissions) { permission ->
                        PermissionsListItem(
                            hasPermission = true,
                            permissionName = permission.toString()
                        )
                        Divider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onHide) {
                Text("Close")
            }
        }
    )
}

@Composable
fun PermissionsListItem(hasPermission: Boolean, permissionName: String) {
    ListItem(
        leadingContent = {
            when (hasPermission) {
                true -> Icon(
                    Icons.Outlined.CheckCircle,
                    "check circle",
                    modifier = Modifier.size(28.dp),
                    tint = success
                )
                false -> ErrorIcon(Modifier.size(28.dp), tint = danger)
            }
        },
        headlineContent = { Text(permissionName) },
    )
}

@Composable
@Preview
fun InsufficientPermissionsPreview() {
    Apiary_MobileTheme {
        ContentPadding {
            InsufficientPermissions(
                featureName = "Attendance",
                onRefreshRequest = {},
                missingPermissions = listOf(READ_TEAMS_HIDDEN),
                requiredPermissions = listOf(CREATE_ATTENDANCE, READ_USERS, READ_TEAMS_HIDDEN),
            )
        }
    }
}

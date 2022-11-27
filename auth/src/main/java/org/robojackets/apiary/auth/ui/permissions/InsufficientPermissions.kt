package org.robojackets.apiary.auth.ui.permissions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.*
import org.robojackets.apiary.auth.model.Permission
import org.robojackets.apiary.auth.model.Permission.*
import org.robojackets.apiary.base.ui.error.GoToItHelpdesk
import org.robojackets.apiary.base.ui.icons.ErrorIcon
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
            text = "$featureName unavailable",
            style = MaterialTheme.typography.h4
        )
        Text(
            text = "You don't have permission to use this feature. Please ask in #it-helpdesk for assistance.",
            modifier = Modifier.padding(top = 12.dp),
            textAlign = TextAlign.Center,
        )

        Row(Modifier.padding(top = 18.dp)) {
            OutlinedButton(onClick = { onRefreshRequest() }) {
                Text("Try again")
            }
            GoToItHelpdesk(modifier = Modifier.padding(start = 8.dp))
        }

        TextButton(onClick = {
            showPermissionDetailsDialog = true
        }, Modifier.padding(top = 0.dp)) {
            Text("More info")
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
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.onBackground,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PermissionsListItem(hasPermission: Boolean, permissionName: String) {

    ListItem(
        icon = {
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
    ) {
        Text(permissionName)
    }
}

@Composable
@Preview
fun InsufficientPermissionsPreview() {
    ContentPadding {
        InsufficientPermissions(
            featureName = "Attendance",
            onRefreshRequest = {},
            missingPermissions = listOf(READ_TEAMS_HIDDEN),
            requiredPermissions = listOf(CREATE_ATTENDANCE, READ_USERS, READ_TEAMS_HIDDEN),
        )
    }
}

package org.robojackets.apiary.merchandise.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.base.ui.dialog.DetailsDialog
import org.robojackets.apiary.base.ui.theme.Apiary_MobileTheme
import org.robojackets.apiary.base.ui.theme.danger
import org.robojackets.apiary.base.ui.theme.success

@Composable
fun ConfirmDistributionDialog(
    isVisible: Boolean,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    DetailsDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(Icons.Outlined.TaskAlt, contentDescription = null, Modifier.size(40.dp))
        },
        iconContentColor = success,
        title = {
            Text("Confirm pickup?")
        },
        details = listOf(
            { DistributeTo("Kristaps Berzinch") },
            { ItemSizeInfo("Small", "S") }
        ),
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismissRequest()
                },
                // FIXME: Button is too bright in dark mode
                colors = ButtonDefaults.buttonColors(containerColor = success, contentColor = Color.White)
            ) {
                Text("Mark picked up")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Cancel")
            }
        },
        modifier = Modifier.padding(0.dp)
    )
}

@Composable
fun AlreadyPickedUpDialog(
    isVisible: Boolean,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    DetailsDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = null, Modifier.size(40.dp))
        },
        iconContentColor = danger,
        title = {
            Text("Item already picked up")
        },
        details = listOf(
            { DistributeTo("Kristaps Berzinch") },
            { ItemPickupInfo("Already distributed by Zach Slaton on Nov 9, 2022") }
        ),
        dismissButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text("Go back")
            }
        },
        modifier = Modifier.padding(0.dp)
    )
}

@Composable
fun DistributionErrorDialog(
    error: String,
    isVisible: Boolean,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    DetailsDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = null, Modifier.size(40.dp))
        },
        iconContentColor = danger,
        title = {
            Text("Ineligible for item")
        },
        details = listOf(
            { DistributeTo("Kristaps Berzinch") },
            { DistributionErrorDetails(error) }
        ),
        dismissButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text("Go back")
            }
        },
        modifier = Modifier.padding(0.dp)
    )
}

@Composable
private fun DistributeTo(name: String) {
    ListItem(
        leadingContent = {
            Icon(Icons.Outlined.AccountCircle, contentDescription = null)
        },
        headlineContent = {
            Text(name)
        }
    )
}

@Composable
private fun ItemSizeInfo(size: String, abbreviation: String) {
    ListItem(
        leadingContent = {
            Text(abbreviation, style = MaterialTheme.typography.headlineSmall)
        },
        headlineContent = {
            Text(size)
        }
    )
}

// FIXME
@Composable
private fun ItemPickupInfo(details: String) {
    ListItem(
        leadingContent = {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = "Past pickup info")
        },
        headlineContent = {
            Text(details)
        }
    )
}

// FIXME
@Composable
private fun DistributionErrorDetails(details: String) {
    ListItem(
        leadingContent = {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = "Distribution error")
        },
        headlineContent = {
            Text(details)
        }
    )
}

@Preview
@Composable
fun ConfirmDistributionDialogPreview() {
    Apiary_MobileTheme {
        ConfirmDistributionDialog(
            isVisible = true,
            onConfirm = {},
            onDismissRequest = {},
        )
    }
}

@Preview
@Composable
fun AlreadyPickedUpDialogPreview() {
    Apiary_MobileTheme {
        AlreadyPickedUpDialog(
            isVisible = true,
            onConfirm = {},
            onDismissRequest = {},
        )
    }
}

@Preview
@Composable
fun NoPaidTransactionErrorDialogPreview() {
    Apiary_MobileTheme {
        DistributionErrorDialog(
            isVisible = true,
            onConfirm = {},
            onDismissRequest = {},
            error = "This person doesn't have a paid transaction for this item."
        )
    }
}

@Preview
@Composable
fun NotDistributableDialogPreview() {
    Apiary_MobileTheme {
        DistributionErrorDialog(
            isVisible = true,
            onConfirm = {},
            onDismissRequest = {},
            error = "This item cannot be distributed."
        )
    }
}
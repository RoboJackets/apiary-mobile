package org.robojackets.apiary.merchandise.ui.pickup_dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.base.ui.dialog.DetailsDialog
import org.robojackets.apiary.base.ui.theme.danger

@Composable
fun DistributionErrorDialog(
    error: String,
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
        details = listOf { DistributionErrorDetails(error) },
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
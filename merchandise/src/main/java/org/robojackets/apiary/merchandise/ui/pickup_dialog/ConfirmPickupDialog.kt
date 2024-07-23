package org.robojackets.apiary.merchandise.ui.pickup_dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.base.model.ShirtSize
import org.robojackets.apiary.base.ui.dialog.DetailsDialog
import org.robojackets.apiary.base.ui.theme.success

@Composable
fun ConfirmPickupDialog(
    userFullName: String,
    userShirtSize: ShirtSize,
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
            { DistributeTo(userFullName) },
            { ShirtSizeInfo(userShirtSize.toString()) }
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


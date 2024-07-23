package org.robojackets.apiary.merchandise.ui.pickup_dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DistributionErrorDetails(details: String) {
    ListItem(
        leadingContent = {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = "Distribution error")
        },
        headlineContent = {
            Text(details)
        }
    )
}
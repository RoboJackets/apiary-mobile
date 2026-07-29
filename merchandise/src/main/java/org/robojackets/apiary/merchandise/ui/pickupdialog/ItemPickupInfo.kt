package org.robojackets.apiary.merchandise.ui.pickupdialog

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.robojackets.apiary.base.ui.icons.ErrorIcon

@Composable
fun ItemPickupInfo(details: String) {
    ListItem(
        leadingContent = {
            ErrorIcon(contentDescription = "Past pickup info")
        },
        headlineContent = {
            Text(details)
        }
    )
}

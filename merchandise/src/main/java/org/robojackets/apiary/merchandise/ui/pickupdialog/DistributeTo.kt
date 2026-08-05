package org.robojackets.apiary.merchandise.ui.pickupdialog

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.robojackets.apiary.base.ui.icons.AccountCircleIcon

@Composable
fun DistributeTo(name: String) {
    ListItem(
        leadingContent = {
            AccountCircleIcon(contentDescription = "Distribute to")
        },
        headlineContent = {
            Text(name)
        }
    )
}

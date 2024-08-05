package org.robojackets.apiary.merchandise.ui.pickupdialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DistributeTo(name: String) {
    ListItem(
        leadingContent = {
            Icon(Icons.Outlined.AccountCircle, contentDescription = "Distribute to")
        },
        headlineContent = {
            Text(name)
        }
    )
}

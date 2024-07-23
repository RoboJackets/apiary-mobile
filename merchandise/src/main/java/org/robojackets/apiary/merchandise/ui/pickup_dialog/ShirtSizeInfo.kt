package org.robojackets.apiary.merchandise.ui.pickup_dialog

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.robojackets.apiary.base.ui.icons.ApparelIcon

@Composable
fun ShirtSizeInfo(sizeName: String) {
    ListItem(
        leadingContent = {
            ApparelIcon(contentDescription = "Shirt size")
        },
        headlineContent = {
            Text(sizeName)
        }
    )
}
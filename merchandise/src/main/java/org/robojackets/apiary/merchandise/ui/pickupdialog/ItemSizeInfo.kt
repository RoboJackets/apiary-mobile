package org.robojackets.apiary.merchandise.ui.pickupdialog

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.robojackets.apiary.base.ui.icons.ApparelIcon

@Composable
fun ItemSizeInfo(sizeName: String) {
    ListItem(
        leadingContent = {
            ApparelIcon(contentDescription = "Item size")
        },
        headlineContent = {
            Text(sizeName)
        }
    )
}

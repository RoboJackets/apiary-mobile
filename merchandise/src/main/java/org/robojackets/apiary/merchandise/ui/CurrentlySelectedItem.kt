package org.robojackets.apiary.merchandise.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.merchandise.model.MerchandiseItem

@Composable
fun CurrentlySelectedItem(
    item: MerchandiseItem,
    onChangeItem: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.weight(1f, false) // In combination with the text field
            // config below, fill=false keeps the Change button in view even when the
            // selected merch item's name gets ellipsized. See https://stackoverflow.com/a/76758541
        ) {
            Icon(
                Icons.Outlined.Storefront,
                contentDescription = null,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                item.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        TextButton(onClick = onChangeItem) { Text("Change") }
    }
}

@Preview
@Composable
fun PreviewCurrentlySelectedItem() {
    ContentPadding {
        CurrentlySelectedItem(
            MerchandiseItem(
                2,
                "Test item with a super duper long name so it will get cut off",
                true,
            )
        ) {}
    }
}

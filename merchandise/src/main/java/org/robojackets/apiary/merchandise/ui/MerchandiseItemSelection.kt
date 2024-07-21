package org.robojackets.apiary.merchandise.ui

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.robojackets.apiary.base.ui.form.ItemList
import org.robojackets.apiary.merchandise.model.MerchandiseItem


@Composable
fun MerchandiseItemSelection(
    title: @Composable () -> Unit,
    items: List<MerchandiseItem>,
    onItemSelected: (item: MerchandiseItem) -> Unit,
) {
    if (items.isEmpty()) {
        // todo
        return
    }

    ItemList(
        items = items,
        onItemSelected = onItemSelected,
        title = title,
        postItem = { idx ->
            if (idx < (items.size - 1)) {
                HorizontalDivider()
            }
        },
    ) {
        Text(it.name)
    }
}

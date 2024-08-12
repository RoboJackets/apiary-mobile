package org.robojackets.apiary.merchandise.ui

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.robojackets.apiary.base.ui.error.ErrorMessageWithRetry
import org.robojackets.apiary.base.ui.form.ItemList
import org.robojackets.apiary.merchandise.model.MerchandiseItem

@Composable
fun MerchandiseItemSelection(
    title: @Composable () -> Unit,
    items: List<MerchandiseItem>?,
    onItemSelected: (item: MerchandiseItem) -> Unit,
    onRefreshList: () -> Unit,
) {
    when {
        items.isNullOrEmpty() -> ErrorMessageWithRetry(
            title = "No merchandise to distribute",
            onRetry = { onRefreshList() },
            prioritizeRetryButton = true,
        )
        else -> ItemList(
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
}

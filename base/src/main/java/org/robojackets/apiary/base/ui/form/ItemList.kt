package org.robojackets.apiary.base.ui.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Suppress("LongParameterList")
@Composable
fun <T> ItemList(
    items: List<T>,
    onItemSelected: (item: T) -> Unit,
    title: @Composable () -> Unit,
    itemKey: (idx: Int) -> Any,
    callout: @Composable () -> Unit = {},
    preItem: @Composable (idx: Int) -> Unit = {},
    postItem: @Composable (idx: Int) -> Unit = {},
    empty: @Composable () -> Unit = {},
    itemContent: @Composable (item: T) -> Unit,
) {
    Column {
        title()
        callout()
        when {
            items.isEmpty() -> empty()
            else -> LazyColumn {
                items(
                    count = items.size,
                    key = itemKey,
                    itemContent = { idx ->
                        val item = items[idx]
                        preItem(idx)
                        ListItem(
                            headlineContent = { itemContent(item) },
                            Modifier.clickable {
                                onItemSelected(item)
                            }
                        )
                        postItem(idx)
                    }
                )
            }
        }
    }
}

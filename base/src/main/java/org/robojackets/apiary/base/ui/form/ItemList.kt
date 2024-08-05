package org.robojackets.apiary.base.ui.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> ItemList(
    items: List<T>,
    onItemSelected: (item: T) -> Unit,
    title: @Composable () -> Unit,
    callout: @Composable () -> Unit = {},
    preItem: @Composable (idx: Int) -> Unit = {},
    postItem: @Composable (idx: Int) -> Unit = {},
    itemContent: @Composable (item: T) -> Unit,
) {
    Column {
        title()
        callout()
        LazyColumn {
            itemsIndexed(items) { idx, item ->
                preItem(idx)
                ListItem(
                    headlineContent = { itemContent(item) },
                    Modifier.clickable {
                        onItemSelected(item)
                    }
                )
                postItem(idx)
            }
        }
    }
}

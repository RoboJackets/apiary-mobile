package org.robojackets.apiary.merchandise.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.merchandise.model.MerchandiseItem
import org.robojackets.apiary.merchandise.model.MerchandiseViewModel

@Composable
fun MerchandiseDistributionScreen(
    viewModel: MerchandiseViewModel,
    merchandiseItemId: Int,
) {
    LaunchedEffect(merchandiseItemId) {
        viewModel.loadMerchandiseItems(selectedItemId = merchandiseItemId)
    }

    val state by viewModel.state.collectAsState()

    ContentPadding {
        Column() {
            Text("Record merchandise distribution", style = MaterialTheme.typography.headlineSmall)
            when(state.selectedItem) {
                null -> Text("No merchandise item selected")
                else -> CurrentlySelectedItem(
                    item = state.selectedItem!!,
                    onChangeItem = { viewModel.navigateToMerchandiseIndex() }
                )
            }
            HorizontalDivider()
        }
    }
}

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
            Icon(Icons.Outlined.Storefront, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
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
fun MerchandiseDistributionScreenPreview() {
    ContentPadding {
        Column() {
            Text("Record merchandise distribution", style = MaterialTheme.typography.headlineSmall)
            CurrentlySelectedItem(
                MerchandiseItem(
                    2,
                    "Test item with a super duper long name so it will get cut off"
                )
            ) {}
            HorizontalDivider()
        }
    }
}


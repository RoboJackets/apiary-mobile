package org.robojackets.apiary.merchandise.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.robojackets.apiary.base.ui.error.ErrorMessageWithRetry
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.base.ui.util.LoadingSpinner
import org.robojackets.apiary.merchandise.model.MerchandiseViewModel

@Composable
fun MerchandiseIndexScreen(
    viewModel: MerchandiseViewModel,
) {
    LaunchedEffect("merch") {
        viewModel.loadMerchandiseItems()
    }

    val state by viewModel.state.collectAsState()

    // ContentPadding ensures the outer container fills the entire available width + height.
    // This is important for navigation to avoid weird animations/effects when moving between
    // screens, even with nav transition animations disabled.
    ContentPadding {
        Column {
            when {
                state.merchandiseItemsListError != null ->
                    ErrorMessageWithRetry(
                        title = state.merchandiseItemsListError
                            ?: "Unable to load merchandise items available for distribution",
                        onRetry = { viewModel.loadMerchandiseItems(forceRefresh = true) },
                        prioritizeRetryButton = true,
                    )

                state.merchandiseItems == null || state.loadingMerchandiseItems -> LoadingSpinner()
                else -> MerchandiseItemSelection(
                    title = {
                        Text(
                            "Pick a merchandise item to distribute",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    items = state.merchandiseItems,
                    onItemSelected = {
                        viewModel.navigateToMerchandiseItemDistribution(it)
                    },
                    onRefreshList = {
                        viewModel.loadMerchandiseItems(forceRefresh = true)
                    },
                )
            }
        }
    }
}

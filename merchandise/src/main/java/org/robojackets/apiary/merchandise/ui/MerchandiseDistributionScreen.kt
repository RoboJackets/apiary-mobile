package org.robojackets.apiary.merchandise.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nxp.nfclib.NxpNfcLib
import org.robojackets.apiary.base.ui.error.ErrorMessageWithRetry
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.base.ui.util.LoadingSpinner
import org.robojackets.apiary.merchandise.model.MerchandiseViewModel
import timber.log.Timber

@Composable
fun MerchandiseDistributionScreen(
    viewModel: MerchandiseViewModel,
    nfcLib: NxpNfcLib,
    merchandiseItemId: Int,
) {
    LaunchedEffect(merchandiseItemId) {
        Timber.d("Launched effect: $merchandiseItemId")
        viewModel.loadMerchandiseItems(selectedItemId = merchandiseItemId)
    }

    val state by viewModel.state.collectAsState()

    ContentPadding {
        when {
            state.merchandiseItemsListError != null -> {
                ErrorMessageWithRetry(
                    title = "Failed to load merchandise item",
                    onRetry = {
                        viewModel.loadMerchandiseItems(
                            forceRefresh = true,
                            selectedItemId = merchandiseItemId
                        )
                              },
                    prioritizeRetryButton = true,
                )
            }
            state.loadingMerchandiseItems || state.selectedItem == null -> LoadingSpinner()
            else -> MerchandiseDistribution(
                state = state,
                nfcLib = nfcLib,
                onBuzzcardTap = {
                    viewModel.onBuzzCardTap(it)
                },
                onConfirmPickup = { viewModel.confirmPickup() },
                onDismissPickupDialog = { viewModel.dismissPickupDialog() },
                onNavigateToMerchandiseIndex = {
                    viewModel.navigateToMerchandiseIndex()
                                               },
            )
        }
    }
}

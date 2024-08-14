package org.robojackets.apiary.merchandise.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nxp.nfclib.NxpNfcLib
import org.robojackets.apiary.base.ui.ActionPrompt
import org.robojackets.apiary.base.ui.IconWithText
import org.robojackets.apiary.base.ui.icons.PendingIcon
import org.robojackets.apiary.base.ui.icons.WarningIcon
import org.robojackets.apiary.base.ui.nfc.BuzzCardPrompt
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.base.ui.theme.danger
import org.robojackets.apiary.merchandise.model.MerchandiseDistributionScreenState
import org.robojackets.apiary.merchandise.model.MerchandiseState

@Suppress("LongMethod")
@Composable
fun MerchandiseDistribution(
    state: MerchandiseState,
    nfcLib: NxpNfcLib,
    onBuzzcardTap: (buzzcardTap: BuzzCardTap) -> Unit,
    onConfirmPickup: () -> Unit,
    onDismissPickupDialog: () -> Unit,
    onNavigateToMerchandiseIndex: () -> Unit,
) {
    MerchandiseDialog(
        state = state,
        onConfirmPickup = onConfirmPickup,
        onDismissPickupDialog = onDismissPickupDialog
    )

    if (state.selectedItem == null) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            IconWithText(
                { WarningIcon(tint = danger) },
                "There's no merchandise item selected. Try going back and selecting an item again",
                TextAlign.Center
            )
            Button(onClick = {
                onNavigateToMerchandiseIndex()
            }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Go back")
            }
        }
        return
    }

    Column {
        Text("Record merchandise distribution", style = MaterialTheme.typography.headlineSmall)
        when (state.selectedItem) {
            null -> Text("No merchandise item selected")
            else -> CurrentlySelectedItem(
                item = state.selectedItem,
                onChangeItem = onNavigateToMerchandiseIndex
            )
        }
        HorizontalDivider()

        Column(
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxSize()
        ) {

            BuzzCardPrompt(
                hidePrompt =
                state.screenState == MerchandiseDistributionScreenState.LoadingDistributionStatus,
                nfcLib = nfcLib,
                onBuzzCardTap = {
                    onBuzzcardTap(it)
                },
                externalError = null
            )

            when (state.screenState) {
                MerchandiseDistributionScreenState.LoadingDistributionStatus -> {
                    ActionPrompt(icon = {
                        PendingIcon(Modifier.size(114.dp))
                    }, title = "Processing...")
                }
                MerchandiseDistributionScreenState.SavingPickupStatus -> {
                    ActionPrompt(icon = {
                        PendingIcon(Modifier.size(114.dp))
                    }, title = "Processing...")
                }
                else -> {}
            }
        }
    }
}

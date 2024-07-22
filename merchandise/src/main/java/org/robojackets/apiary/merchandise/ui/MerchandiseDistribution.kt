package org.robojackets.apiary.merchandise.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.nxp.nfclib.NxpNfcLib
import org.robojackets.apiary.base.ui.nfc.BuzzCardPrompt
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.merchandise.model.MerchandiseState
import timber.log.Timber

@Composable
fun MerchandiseDistribution(
    state: MerchandiseState,
    nfcLib: NxpNfcLib,
    onBuzzcardTap: (buzzcardTap: BuzzCardTap) -> Unit,
    onNavigateToMerchandiseIndex: () -> Unit,
) {
    ConfirmDistributionDialog(
        isVisible = true,
        onConfirm = { },
    ) { }
    Column() {
        Text("Record merchandise distribution", style = MaterialTheme.typography.headlineSmall)
        when(state.selectedItem) {
            null -> Text("No merchandise item selected")
            else -> CurrentlySelectedItem(
                item = state.selectedItem,
                onChangeItem = onNavigateToMerchandiseIndex
            )
        }
        HorizontalDivider()

        // TODO: the rest of the handling
        BuzzCardPrompt(
            hidePrompt = false,
            nfcLib = nfcLib,
            onBuzzCardTap = {
                Timber.d("Buzzcard tapped: ${it.gtid}, ${it.source}")
                onBuzzcardTap(it)
            },
            externalError = null
        )
    }
}

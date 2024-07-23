package org.robojackets.apiary.merchandise.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.nxp.nfclib.NxpNfcLib
import org.robojackets.apiary.base.ui.nfc.BuzzCardPrompt
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.merchandise.model.MerchandiseDistributionScreenState
import org.robojackets.apiary.merchandise.model.MerchandiseState
import org.robojackets.apiary.merchandise.ui.pickup_dialog.AlreadyPickedUpDialog
import org.robojackets.apiary.merchandise.ui.pickup_dialog.ConfirmPickupDialog
import timber.log.Timber

@Composable
fun MerchandiseDistribution(
    state: MerchandiseState,
    nfcLib: NxpNfcLib,
    onBuzzcardTap: (buzzcardTap: BuzzCardTap) -> Unit,
    onConfirmPickup: () -> Unit,
    onDismissPickupDialog: () -> Unit,
    onNavigateToMerchandiseIndex: () -> Unit,
) {
    when(state.screenState) {
        MerchandiseDistributionScreenState.ShowStatusDialog -> {
          if (state.lastDistributionStatus != null) {
              if (state.lastDistributionStatus.can_distribute) {
                  ConfirmPickupDialog(
                        userFullName = state.lastDistributionStatus.user.name,
                        userShirtSize = state.lastDistributionStatus.user.shirt_size!!, // FIXME
                        onConfirm = { onConfirmPickup() },
                        onDismissRequest = { onDismissPickupDialog() }
                  )
              } else if (!state.lastDistributionStatus.can_distribute) {
                  AlreadyPickedUpDialog(
                      distributeTo = state.lastDistributionStatus.user,
                      providedBy = state.lastDistributionStatus.distribution.provided_by!!, // FIXME: Remove these !!s
                      providedAt = state.lastDistributionStatus.distribution.provided_at!!.toInstant(),
                      onDismissRequest = onDismissPickupDialog
                  )
              }
          }
        }
        else -> {}
    }

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

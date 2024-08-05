package org.robojackets.apiary.merchandise.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nxp.nfclib.NxpNfcLib
import org.robojackets.apiary.base.ui.ActionPrompt
import org.robojackets.apiary.base.ui.icons.PendingIcon
import org.robojackets.apiary.base.ui.nfc.BuzzCardPrompt
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.merchandise.model.MerchandiseDistributionScreenState
import org.robojackets.apiary.merchandise.model.MerchandiseState
import org.robojackets.apiary.merchandise.ui.pickupdialog.AlreadyPickedUpDialog
import org.robojackets.apiary.merchandise.ui.pickupdialog.ConfirmPickupDialog
import org.robojackets.apiary.merchandise.ui.pickupdialog.DistributionErrorDialog

// TODO(before merge): Shorten method and implement empty ifs, then remove next line
@Suppress("LongMethod", "CyclomaticComplexMethod", "EmptyIfBlock")
@Composable
fun MerchandiseDistribution(
    state: MerchandiseState,
    nfcLib: NxpNfcLib,
    onBuzzcardTap: (buzzcardTap: BuzzCardTap) -> Unit,
    onConfirmPickup: () -> Unit,
    onDismissPickupDialog: () -> Unit,
    onNavigateToMerchandiseIndex: () -> Unit,
) {
    // This when handles showing dialogs on screen
    when (state.screenState) {
        MerchandiseDistributionScreenState.ShowPickupStatusDialog -> {
          if (state.lastDistributionStatus != null) {
              if (state.lastDistributionStatus.canDistribute) {
                  ConfirmPickupDialog(
                        userFullName = state.lastDistributionStatus.user.name,
                        userShirtSize = state.lastDistributionStatus.distribution.size,
                        onConfirm = { onConfirmPickup() },
                        onDismissRequest = { onDismissPickupDialog() },
                  )
              } else if (!state.lastDistributionStatus.canDistribute) {
                  AlreadyPickedUpDialog(
                      distributeTo = state.lastDistributionStatus.user,
                      // TODO(before merge): Remove these !!s
                      providedBy = state.lastDistributionStatus.distribution.providedBy!!,
                      providedAt =
                        state.lastDistributionStatus.distribution.providedAt!!.toInstant(),
                      onDismissRequest = onDismissPickupDialog,
                  )
              }
          } else if (state.error != null) {
              DistributionErrorDialog(
                  error = state.error,
                  onDismissRequest = onDismissPickupDialog,
              )
          }
        }
        MerchandiseDistributionScreenState.ShowDistributionErrorDialog -> {
            if (state.error != null) {
                DistributionErrorDialog(
                    error = state.error,
                    title = "Distribution error",
                    onDismissRequest = onDismissPickupDialog,
                )
            }
        }
        else -> {}
    }

    if (state.selectedItem == null) {
        // TODO(before merge): implement this
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
        if (state.lastStorePickupStatus != null) {
            // TODO: show toast
        }

        Column(
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxSize()
        ) {

            BuzzCardPrompt(
                hidePrompt = state.screenState == MerchandiseDistributionScreenState.LoadingDistributionStatus,
                nfcLib = nfcLib,
                onBuzzCardTap = {
                    onBuzzcardTap(it)
                },
                externalError = null
            )

            when (state.screenState) {
                MerchandiseDistributionScreenState.LoadingDistributionStatus -> {
                    ActionPrompt(icon = { PendingIcon(Modifier.size(114.dp)) }, title = "Processing...")
                }
                MerchandiseDistributionScreenState.SavingPickupStatus -> {
                    ActionPrompt(icon = { PendingIcon(Modifier.size(114.dp)) }, title = "Processing...")
                }
                else -> {}
            }
        }
    }
}

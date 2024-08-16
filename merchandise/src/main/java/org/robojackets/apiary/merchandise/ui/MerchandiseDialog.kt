package org.robojackets.apiary.merchandise.ui

import androidx.compose.runtime.Composable
import org.robojackets.apiary.merchandise.model.MerchandiseDistributionScreenState
import org.robojackets.apiary.merchandise.model.MerchandiseState
import org.robojackets.apiary.merchandise.ui.pickupdialog.AlreadyPickedUpDialog
import org.robojackets.apiary.merchandise.ui.pickupdialog.ConfirmPickupDialog
import org.robojackets.apiary.merchandise.ui.pickupdialog.DistributionErrorDialog

@Composable
fun MerchandiseDialog(
    state: MerchandiseState,
    onConfirmPickup: () -> Unit,
    onDismissPickupDialog: () -> Unit,
) {
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
                        providedBy = state.lastDistributionStatus.distribution.providedBy,
                        providedAt =
                        state.lastDistributionStatus.distribution.providedAt?.toInstant(),
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
}

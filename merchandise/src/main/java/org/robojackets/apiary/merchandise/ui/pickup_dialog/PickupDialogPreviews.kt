package org.robojackets.apiary.merchandise.ui.pickup_dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.robojackets.apiary.base.model.BasicUser
import org.robojackets.apiary.base.model.ShirtSize
import org.robojackets.apiary.base.model.UserRef
import org.robojackets.apiary.base.ui.theme.Apiary_MobileTheme
import java.time.Instant

@Preview
@Composable
fun ConfirmDistributionDialogPreview() {
    Apiary_MobileTheme {
        ConfirmPickupDialog(
            userFullName = "Kristaps Berzinch",
            userShirtSize = ShirtSize.SMALL,
            onConfirm = {},
            onDismissRequest = {},
        )
    }
}

@Preview
@Composable
fun NoPaidTransactionErrorDialogPreview() {
    Apiary_MobileTheme {
        DistributionErrorDialog(
            onDismissRequest = {},
            error = "This person doesn't have a paid transaction for this item."
        )
    }
}

@Preview
@Composable
fun NotDistributableDialogPreview() {
    Apiary_MobileTheme {
        DistributionErrorDialog(
            onDismissRequest = {},
            error = "This item cannot be distributed."
        )
    }
}

@Preview
@Composable
fun AlreadyPickedUpDialogPreview() {
    Apiary_MobileTheme {
        AlreadyPickedUpDialog(
            distributeTo = BasicUser(
                id = 18,
                uid = "gburdell3",
                name = "George Burdell",
                preferred_first_name = "George",
                shirt_size = ShirtSize.SMALL,
                polo_size = ShirtSize.SMALL,
            ),
            onDismissRequest = {},
            providedBy = UserRef(full_name = "Zach Slaton", id = 3),
            providedAt = Instant.now()
        )
    }
}
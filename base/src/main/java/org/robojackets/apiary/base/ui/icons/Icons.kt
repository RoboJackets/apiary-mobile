@file:Suppress("TooManyFunctions")

package org.robojackets.apiary.base.ui.icons

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import org.robojackets.apiary.base.R

@Composable
fun BluetoothSettingsIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String? = null,
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_baseline_settings_bluetooth_24dp),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}


@Composable
fun ContactlessIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "NFC",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_contactless_24dp),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

@Composable
fun WarningIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "warning",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_baseline_warning_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun CheckCircleIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "check circle",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_check_circle_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun ErrorIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "error"
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_baseline_error_outline_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun CreditCardIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "credit card",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_baseline_credit_card_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun PendingIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "pending",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_pending_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun GroupsIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "groups",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_groups_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun EventIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "event",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_event_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun UpdateIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "update",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_baseline_update_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun ApparelIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "apparel",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_apparel_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun StorefrontIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "storefront",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_storefront_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun TaskAltIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "task",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_task_alt_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun AccountCircleIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "account",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_account_circle_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun VerifiedUserIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "verified user",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_verified_user_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun LogoutIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "logout",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_logout_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun HomeIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "home",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_home_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun DeployedCodeIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "deployed code",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_deployed_code_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun FeedbackIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "feedback",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_feedback_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun PrivacyTipIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String = "privacy tip",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_privacy_tip_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun InfoIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String? = null,
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_info_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
fun SettingsIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String? = null,
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_settings_24dp),
        tint = tint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

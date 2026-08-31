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
fun DynamicBatteryIcon(
    modifier: Modifier = Modifier,
    normalTint: Color = MaterialTheme.colorScheme.onSurface,
    lowTint: Color = MaterialTheme.colorScheme.error,
    batteryLevel: Int,
    isCharging: Boolean,
    contentDescription: String? = null,
    lowBatteryThreshold: Int = 15,
) {
    var iconId = R.drawable.ic_outline_battery_android_full_24dp

    when (isCharging) {
        true -> iconId = R.drawable.ic_outline_battery_android_frame_bolt_24dp
        false -> when (batteryLevel) {
            in 96..100 -> iconId = R.drawable.ic_outline_battery_android_full_24dp
            in 86..95 -> iconId = R.drawable.ic_outline_battery_android_6_24dp
            in 71..85 -> iconId = R.drawable.ic_outline_battery_android_5_24dp
            in 51..70 -> iconId = R.drawable.ic_outline_battery_android_4_24dp
            in 36..50 -> iconId = R.drawable.ic_outline_battery_android_3_24dp
            in 16..30 -> iconId = R.drawable.ic_outline_battery_android_2_24dp
            in 6..15 -> iconId = R.drawable.ic_outline_battery_android_1_24dp
            in 0..5 -> iconId = R.drawable.ic_outline_battery_android_0_24dp
        }
    }

    Icon(
        painter = painterResource(id = iconId),
        tint = if (batteryLevel <= lowBatteryThreshold) lowTint else normalTint,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}


@Composable
fun BluetoothIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String? = null,
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_bluetooth_24dp),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

@Composable
fun BluetoothDisabledIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String? = null,
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_bluetooth_disabled_24dp),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

@Composable
fun BluetoothConnectedIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String? = null,
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_bluetooth_connected_24dp),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
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
fun IdCardIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String? = null,
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_id_card_24dp),
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
fun Mrd5Icon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String? = null,
) {
    Icon(
        painter = painterResource(id = R.drawable.mrd5),
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

package org.robojackets.apiary.merchandise.ui.pickup_dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.base.model.BasicUser
import org.robojackets.apiary.base.model.UserRef
import org.robojackets.apiary.base.ui.dialog.DetailsDialog
import org.robojackets.apiary.base.ui.theme.danger
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import java.util.TimeZone


@Composable
fun AlreadyPickedUpDialog(
    distributeTo: BasicUser,
    providedBy: UserRef,
    providedAt: Instant,
    onDismissRequest: () -> Unit,
) {
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
    val localProvidedAt = LocalDateTime.ofInstant(providedAt, TimeZone.getDefault().toZoneId())

    DetailsDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = null, Modifier.size(40.dp))
        },
        iconContentColor = danger,
        title = {
            Text("Item already picked up")
        },
        details = listOf(
            { DistributeTo(distributeTo.name) },
            { ItemPickupInfo("Distributed by ${providedBy.full_name} on ${localProvidedAt.format(dateFormatter)}") }
        ),
        dismissButton = {
            Button(
                onClick = onDismissRequest) {
                Text("Go back")
            }
        },
        modifier = Modifier.padding(0.dp)
    )
}

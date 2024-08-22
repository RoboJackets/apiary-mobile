package org.robojackets.apiary.base.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Suppress("LongParameterList")
@Composable
fun DetailsDialog(
    icon: @Composable () -> Unit,
    iconContentColor: Color,
    title: @Composable () -> Unit,
    details: List<@Composable () -> Unit>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButton: @Composable () -> Unit = {},
    dismissButton: @Composable () -> Unit,
    detailsShouldRender: (idx: Int) -> Boolean = { true }
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = icon,
        iconContentColor = iconContentColor,
        title = title,
        text = {
            Column {
                details.forEachIndexed { idx, it ->
                    if (detailsShouldRender(idx)) {
                        HorizontalDivider()
                        it()
                    }
                }
                HorizontalDivider()
            }
        },
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        modifier = modifier,
    )
}

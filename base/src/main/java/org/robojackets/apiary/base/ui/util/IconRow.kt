package org.robojackets.apiary.base.ui.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun IconRow(
    icon: @Composable (() -> Unit)?,
    text: String,
    button: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 49.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.weight(1f, false) // In combination with the text field
                // config below, fill=false keeps the Change button in view even when the
                // selected item's name gets ellipsized. See https://stackoverflow.com/a/76758541
            ) {
                icon?.let {
                    Box(Modifier.padding(end = 4.dp)) {
                        icon()
                    }
                }
                Text(
                    text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            button()
        }
        if (showDivider) {
            HorizontalDivider()
        }
    }
}

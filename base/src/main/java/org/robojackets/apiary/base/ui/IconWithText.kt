package org.robojackets.apiary.base.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun IconWithText(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        icon()
        text()
    }
}


@Composable
fun IconWithText(
    icon: @Composable () -> Unit,
    text: String,
    textAlign: TextAlign = TextAlign.Center
) {
    IconWithText(
        icon,
        { Text(text = text, modifier = Modifier.padding(start = 4.dp), textAlign = textAlign) }
    )
}

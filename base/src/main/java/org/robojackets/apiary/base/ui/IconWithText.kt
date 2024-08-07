package org.robojackets.apiary.base.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun IconWithText(
    icon: @Composable () -> Unit,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    text: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement,
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
        icon
    ) { Text(text = text, modifier = Modifier.padding(start = 4.dp), textAlign = textAlign) }
}

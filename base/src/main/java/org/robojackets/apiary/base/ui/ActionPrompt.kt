package org.robojackets.apiary.base.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.base.ui.icons.ContactlessIcon
import org.robojackets.apiary.base.ui.theme.danger

@Composable
fun ActionPrompt(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String = "",
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        icon()
        Text(
            text = title,
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(top = 6.dp),
            textAlign = TextAlign.Center,
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
fun ActionPromptTapABuzzCardPreview() {
    ActionPrompt(
        icon = { ContactlessIcon(Modifier.size(114.dp)) },
        title = "Tap a BuzzCard",
    )
}

@Preview
@Composable
fun ActionPromptCardReadError() {
    ActionPrompt(
        icon = { ContactlessIcon(Modifier.size(114.dp), tint = danger) },
        title = "Card read error",
        subtitle = "Make sure to hold the BuzzCard in place for a few seconds"
    )
}
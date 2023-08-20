package org.robojackets.apiary.base.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.base.ui.icons.ContactlessIcon
import org.robojackets.apiary.base.ui.icons.WarningIcon
import org.robojackets.apiary.base.ui.theme.danger

@Composable
fun ActionPrompt(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String? = "",
    customContent: (@Composable () -> Unit)? = { },
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        icon()
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 6.dp),
            textAlign = TextAlign.Center,
        )

        if (customContent != null) {
            customContent()
        }

        subtitle?.let {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
        }
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

@Preview
@Composable
fun ActionPromptCardReadErrorWrongType() {
    ActionPrompt(
        icon = { ContactlessIcon(Modifier.size(114.dp), tint = danger) },
        title = "Card read error",
        subtitle = "Try tapping again"
    ) {
        IconWithText(icon = { WarningIcon(tint = danger) },
            text = "We only support BuzzCards 😉")
    }
}

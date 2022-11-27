package org.robojackets.apiary.base.ui.callout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.base.ui.IconWithText
import org.robojackets.apiary.base.ui.icons.ErrorIcon
import org.robojackets.apiary.base.ui.theme.warningDarkSubtle
import org.robojackets.apiary.base.ui.theme.warningLightMuted
import org.robojackets.apiary.base.ui.theme.warningLightSubtle

@Composable
fun Callout(
    title: @Composable () -> Unit,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    padding: PaddingValues? = PaddingValues(8.dp),
    body: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .clip(shape = shape)
            .then(if (borderColor != null) Modifier.border(1.dp, borderColor, shape) else Modifier)
            .then(if (backgroundColor != null) Modifier.background(backgroundColor) else Modifier)
            .then(if (padding != null) Modifier.padding(paddingValues = padding) else Modifier)
            .fillMaxWidth(),
    ) {
        title()
        body()
    }
}

@Composable
fun WarningCallout(
    titleText: String,
    padding: PaddingValues? = null,
    body: @Composable () -> Unit,
) {
    val isLightTheme = MaterialTheme.colors.isLight

    Callout(
        title = {
            IconWithText(icon = { ErrorIcon() }, horizontalArrangement = Arrangement.Start) {
                Text(
                    titleText,
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.h6
                )
            }
        },
        backgroundColor = if (isLightTheme) warningLightSubtle else warningDarkSubtle,
        borderColor = if (isLightTheme) warningLightMuted else warningLightMuted,
        padding = padding,
    ) {
        body()
    }
}

@Suppress("UnusedPrivateMember")
@Preview
@Composable
private fun WarningCalloutPreview() {
    WarningCallout(
        titleText = "Some teams are hidden",
    ) {
        Text("Your account doesn't have permission to view all teams, including training " +
                "teams. Ask in #it-helpdesk for access.")
    }
}

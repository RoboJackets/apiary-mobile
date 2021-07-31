package org.robojackets.apiary.base.ui.icons

import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import org.robojackets.apiary.base.R

@Composable
fun ContactlessIcon(
    modifier: Modifier,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_outline_contactless_24),
        contentDescription = "NFC symbol",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun WarningIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
) {
    Icon(
        Icons.Default.Warning,
        tint = tint,
        modifier = modifier,
        contentDescription = "warning"
    )
}

@Composable
fun ErrorIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_baseline_error_outline_24),
        tint = tint,
        modifier = modifier,
        contentDescription = "error"
    )
}
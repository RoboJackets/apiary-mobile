package org.robojackets.apiary.ui.global

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.base.ui.IconWithText
import org.robojackets.apiary.base.ui.icons.WarningIcon

@Composable
fun AppTopBar(isProdEnv: Boolean) {
    Column {
        TopAppBar(
            title = {
                Text(
                    text = "MyRoboJackets",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.W800
                )
            },
        )

        if (!isProdEnv) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.error)
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 4.dp)
            ) {
                IconWithText(
                    icon = { WarningIcon(tint = Color.White) },
                    text = { Text(
                        "Non-production server",
                        modifier = Modifier.padding(start = 4.dp),
                        color = Color.White
                    ) }
                )
            }
        }
    }
}

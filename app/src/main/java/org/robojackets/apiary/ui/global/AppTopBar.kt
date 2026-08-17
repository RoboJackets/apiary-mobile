package org.robojackets.apiary.ui.global

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                scrolledContainerColor = Color.Unspecified,
                navigationIconContentColor = Color.Unspecified,
                titleContentColor = Color.Unspecified,
                actionIconContentColor = Color.Unspecified,
                subtitleContentColor = Color.Unspecified
            ),
            title = {
                Text(
                    text = "MyRoboJackets",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.W800
                )
            },
        )

        if (!isProdEnv) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.error)
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 4.dp)
            ) {
                IconWithText(
                    icon = { WarningIcon(tint = MaterialTheme.colorScheme.onError) },
                    text = {
                        Text(
                        "Non-production server",
                        modifier = Modifier.padding(start = 4.dp),
                        color = MaterialTheme.colorScheme.onError
                    )
                    }
                )
            }
        }
    }
}

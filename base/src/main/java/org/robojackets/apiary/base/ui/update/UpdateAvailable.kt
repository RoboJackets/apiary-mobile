package org.robojackets.apiary.base.ui.update

import android.content.res.Configuration.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.base.ui.icons.UpdateIcon
import org.robojackets.apiary.base.ui.util.ContentPadding

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun RequiredUpdatePrompt() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        UpdateIcon(Modifier.size(72.dp))
        Text("Install update to continue", style = MaterialTheme.typography.h3)
        Text("Wow! It's getting a little dusty in here! Before we continue, we need you to " +
                "install the latest version of MyRoboJackets Android.")
        Button(onClick = {}) {
            Text("Download and install update")
        }
        Text(
            "Due to the priority/age of this update, we need you to install it now."
        )

//        Text()
    }
}

@Composable
fun UpdateAvailable(
    required: Boolean,
    onTriggerUpdate: () -> Unit,
    onIgnoreUpdate: () -> Unit,
) {
    ContentPadding {

    }
}

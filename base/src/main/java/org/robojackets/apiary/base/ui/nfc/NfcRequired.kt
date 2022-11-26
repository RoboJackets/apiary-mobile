package org.robojackets.apiary.base.ui.nfc

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.robojackets.apiary.base.ui.ActionPrompt
import org.robojackets.apiary.base.ui.icons.ErrorIcon
import org.robojackets.apiary.base.ui.theme.danger
import org.robojackets.apiary.base.ui.theme.warningLightEmphasis

@Composable
fun NfcRequired(nfcEnabled: Boolean, gatedComposable: @Composable () -> Unit) {
    val context = LocalContext.current
    var enableNfcClicked by remember { mutableStateOf(false) }

    if (nfcEnabled) {
        gatedComposable()
    } else {
        Column(Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (enableNfcClicked) {
                false -> {
                    ActionPrompt(
                        icon = { ErrorIcon(Modifier.size(114.dp), tint = danger) },
                        title = "NFC is disabled",
                        subtitle = "Please enable NFC and restart the app to continue"
                    )
                    Button(onClick = {
                        // Based on https://stackoverflow.com/a/14989772
                        context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                        enableNfcClicked = true
                    }) {
                        Text("Enable NFC")
                    }
                }
                true -> {
                    ActionPrompt(
                        icon = { ErrorIcon(Modifier.size(114.dp), tint = warningLightEmphasis) },
                        title = "Restart to continue",
                        subtitle = "If you've enabled NFC, just restart the app and you'll be on your way!"
                    )
                }
            }
        }
    }
}

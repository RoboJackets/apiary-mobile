package org.robojackets.apiary.ui.settings

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.alorma.settings.composables.SettingsMenuLink
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.robojackets.apiary.BuildConfig
import org.robojackets.apiary.base.AppEnvironment
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.base.ui.util.MadeWithLove

@Composable
 private fun Settings(
    appEnv: AppEnvironment,
    onLogout: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            SettingsHeader("Account")
            SettingsMenuLink(
                icon = { Icon(Icons.Outlined.Person, contentDescription = "person") },
                title = { Text(text = "George Burdell") },
                subtitle = { Text(text = "gburdell3") },
                onClick = {}
            )
            SettingsMenuLink(
                icon = { Icon(Icons.Outlined.Logout, contentDescription = "logout") },
                title = { Text(text = "Logout") },
                onClick = onLogout
            )
            SettingsHeader("About")
            SettingsMenuLink(
                icon = { Icon(Icons.Outlined.Home, contentDescription = "home") },
                title = { Text(text = "Server") },
                subtitle = { Text(
                    text = "${appEnv.name} (${appEnv.apiBaseUrl})"
                )},
                onClick = {}
            )
            SettingsMenuLink(
                icon = { Icon(Icons.Outlined.Build, contentDescription = "build") },
                title = { Text(text = "Version") },
                subtitle = { Text(
                    text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                )},
                onClick = {}
            )
            SettingsMenuLink(
                icon = { Icon(Icons.Outlined.PrivacyTip, contentDescription = "privacy tip") },
                title = { Text(text = "Privacy policy") },
                onClick = {}
            )
            SettingsMenuLink(
                icon = { Icon(Icons.Outlined.Info, contentDescription = "info") },
                title = { Text(text = "Open-source licenses") },
                onClick = {
                    context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                },
            )
        }
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MadeWithLove()
        }
    }


 }

@Composable
private fun SettingsHeader(headerText: String) {
    Text(text = headerText, fontWeight = FontWeight.Bold)
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    ContentPadding {
       Settings(
           appEnv = viewModel.globalSettings.appEnv,
           onLogout = {
               viewModel.logout()
           }
       )
    }
}

@Preview
@Composable
private fun SettingsPreview() {
    Settings(onLogout = {}, appEnv = AppEnvironment.Production)
}
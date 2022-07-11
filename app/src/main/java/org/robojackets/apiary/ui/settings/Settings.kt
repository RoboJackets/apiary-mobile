package org.robojackets.apiary.ui.settings

import android.content.Intent
import androidx.browser.customtabs.CustomTabsClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.robojackets.apiary.BuildConfig
import org.robojackets.apiary.auth.model.UserInfo
import org.robojackets.apiary.base.AppEnvironment
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.base.ui.util.MadeWithLove
import org.robojackets.apiary.base.ui.util.UpdateStatus

@Suppress("LongMethod")
@Composable
 private fun Settings(
     appEnv: AppEnvironment,
     user: UserInfo?,
     onLogout: () -> Unit,
     onOpenPrivacyPolicy: () -> Unit,
     onOpenMakeAWish: () -> Unit,
 ) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            SettingsHeader("Account")
            SettingsMenuLink(
                icon = { Icon(Icons.Outlined.Person, contentDescription = "person") },
                title = { Text(text = user?.name ?: "Refreshing data...") },
                subtitle = { Text(text = user?.uid ?: "") },
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
                ) },
                onClick = {}
            )
            SettingsMenuLink(
                icon = { Icon(Icons.Outlined.Build, contentDescription = "build") },
                title = { Text(text = "Version") },
                subtitle = { Text(
                    text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                ) },
                onClick = {}
            )
            SettingsMenuLink(
                icon = { Icon(Icons.Outlined.Update, contentDescription = "update") },
                title = { Text("App update status") },
                subtitle = {
                    UpdateStatus()
                },
                onClick = {}
            )
            SettingsMenuLink(
                icon = { Icon(Icons.Outlined.Feedback, contentDescription = "feedback") },
                title = { Text(text = "Make a wish") },
                onClick = onOpenMakeAWish,
            )
            SettingsMenuLink(
                icon = { Icon(Icons.Outlined.PrivacyTip, contentDescription = "privacy tip") },
                title = { Text(text = "Privacy policy") },
                onClick = onOpenPrivacyPolicy,
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
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        // The package name below is NOT the app package name...sigh
        // Thanks to https://stackoverflow.com/a/62183713
        CustomTabsClient.bindCustomTabsService(context, "com.android.chrome", viewModel.customTabsServiceConnection)
        viewModel.getServerInfo()
        viewModel.getUser()
    }

    val state by viewModel.state.collectAsState()
    val secondaryThemeColor = MaterialTheme.colors.background
    ContentPadding {
       Settings(
           appEnv = viewModel.globalSettings.appEnv,
           user = state.user,
           onLogout = {
               viewModel.logout()
           },
           onOpenPrivacyPolicy = {
               val customTabsIntent = viewModel.getCustomTabsIntent()
               customTabsIntent.launchUrl(context, viewModel.privacyPolicyUrl)
           },
           onOpenMakeAWish = {
               val customTabsIntent = viewModel.getCustomTabsIntent(secondaryThemeColor.toArgb())
               customTabsIntent.launchUrl(context, viewModel.makeAWishUrl)
           }
       )
    }
}

@Suppress("UnusedPrivateMember")
@Preview
@Composable
private fun SettingsPreview() {
    Settings(
        appEnv = AppEnvironment.Production,
        user = null,
        onLogout = {},
        onOpenPrivacyPolicy = {},
        onOpenMakeAWish = {}
    )
}

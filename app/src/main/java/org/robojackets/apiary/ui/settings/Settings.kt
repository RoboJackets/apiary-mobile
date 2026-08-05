package org.robojackets.apiary.ui.settings

import android.content.Intent
import androidx.browser.customtabs.CustomTabsClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.google.android.gms.oss.licenses.v2.OssLicensesMenuActivity
import org.robojackets.apiary.BuildConfig
import org.robojackets.apiary.auth.model.UserInfo
import org.robojackets.apiary.base.AppEnvironment
import org.robojackets.apiary.base.ui.icons.AccountCircleIcon
import org.robojackets.apiary.base.ui.icons.DeployedCodeIcon
import org.robojackets.apiary.base.ui.icons.FeedbackIcon
import org.robojackets.apiary.base.ui.icons.HomeIcon
import org.robojackets.apiary.base.ui.icons.InfoIcon
import org.robojackets.apiary.base.ui.icons.LogoutIcon
import org.robojackets.apiary.base.ui.icons.PrivacyTipIcon
import org.robojackets.apiary.base.ui.icons.UpdateIcon
import org.robojackets.apiary.base.ui.icons.VerifiedUserIcon
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.base.ui.util.MadeWithLove
import org.robojackets.apiary.ui.update.UpdateStatus

@Suppress("LongMethod", "LongParameterList")
@Composable
private fun Settings(
     appEnv: AppEnvironment,
     user: UserInfo?,
     onLogout: () -> Unit,
     onOpenPrivacyPolicy: () -> Unit,
     onOpenMakeAWish: () -> Unit,
     onRefreshUser: () -> Unit,
     onNavigateToOptionalUpdateBottomSheet: () -> Unit,
     onNavigateToRequiredUpdatePrompt: () -> Unit,
     onNavigateToUpdateInProgress: () -> Unit,
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
                icon = { AccountCircleIcon(contentDescription = "person") },
                title = { Text(text = user?.name ?: "Refreshing data...") },
                subtitle = { Text(text = user?.uid ?: "") },
                onClick = { onRefreshUser() }
            )
            if (BuildConfig.DEBUG) {
                SettingsMenuLink(
                    icon = { VerifiedUserIcon() },
                    title = { Text(text = "DEBUG: Recognized permissions") },
                    subtitle = { Text(text = user?.allPermissions?.joinToString(separator = ", ") ?: "None") },
                    onClick = { onRefreshUser() }
                )
                SettingsMenuLink(
                    icon = { UpdateIcon() },
                    title = { Text(text = "DEBUG: Open optional update bottom sheet") },
                    onClick = { onNavigateToOptionalUpdateBottomSheet() }
                )
                SettingsMenuLink(
                    icon = { UpdateIcon() },
                    title = { Text(text = "DEBUG: Open required update prompt") },
                    onClick = { onNavigateToRequiredUpdatePrompt() }
                )
                SettingsMenuLink(
                    icon = { UpdateIcon() },
                    title = { Text(text = "DEBUG: Open update in progress screen") },
                    onClick = { onNavigateToUpdateInProgress() }
                )
            }
            SettingsMenuLink(
                icon = { LogoutIcon() },
                title = { Text(text = "Logout") },
                onClick = onLogout
            )
            SettingsHeader("About")
            SettingsMenuLink(
                icon = { HomeIcon(contentDescription = "server") },
                title = { Text(text = "Server") },
                subtitle = {
                    Text(
                    text = "${appEnv.name} (${appEnv.apiBaseUrl})"
                )
                },
                onClick = {}
            )
            SettingsMenuLink(
                icon = { DeployedCodeIcon(contentDescription = "version") },
                title = { Text(text = "Version") },
                subtitle = {
                    Text(
                    text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                )
                },
                onClick = {}
            )
            SettingsMenuLink(
                icon = { UpdateIcon() },
                title = { Text("App update status") },
                subtitle = {
                    UpdateStatus()
                },
                onClick = {}
            )
            SettingsMenuLink(
                icon = { FeedbackIcon() },
                title = { Text(text = "Make a wish") },
                onClick = onOpenMakeAWish,
            )
            SettingsMenuLink(
                icon = { PrivacyTipIcon() },
                title = { Text(text = "Privacy policy") },
                onClick = onOpenPrivacyPolicy,
            )
            SettingsMenuLink(
                icon = { InfoIcon() },
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
    val secondaryThemeColor = MaterialTheme.colorScheme.background
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
           },
           onRefreshUser = {
               viewModel.getUser(forceRefresh = true)
           },
           onNavigateToOptionalUpdateBottomSheet = {
               viewModel.navigateToOptionalUpdateBottomSheet()
           },
           onNavigateToRequiredUpdatePrompt = {
               viewModel.navigateToRequiredUpdatePrompt()
           },
           onNavigateToUpdateInProgress = {
               viewModel.navigateToUpdateInProgress()
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
        onOpenMakeAWish = {},
        onRefreshUser = {},
        onNavigateToOptionalUpdateBottomSheet = {},
        onNavigateToRequiredUpdatePrompt = {},
        onNavigateToUpdateInProgress = {},
    )
}

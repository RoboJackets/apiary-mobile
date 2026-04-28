package org.robojackets.apiary.ui.settings

import android.content.Intent
import androidx.browser.customtabs.CustomTabsClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.robojackets.apiary.BuildConfig
import org.robojackets.apiary.auth.model.UserInfo
import org.robojackets.apiary.base.AppEnvironment
import org.robojackets.apiary.base.ui.theme.danger
import org.robojackets.apiary.base.ui.util.ContentPadding
import org.robojackets.apiary.base.ui.util.MadeWithLove
import org.robojackets.apiary.ui.update.UpdateStatus

@Suppress("LongMethod", "LongParameterList")
@Composable
private fun Settings(
    appEnv: AppEnvironment,
    user: UserInfo?,
    mobileCredentialKeyDraft: String,
    mobileCredentialKeySaveError: String?,
    onMobileCredentialKeyDraftChange: (String) -> Unit,
    onSaveMobileCredentialKey: () -> Unit,
    onClearMobileCredentialKey: () -> Unit,
    onLogout: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenMakeAWish: () -> Unit,
    onRefreshUser: () -> Unit,
    onNavigateToOptionalUpdateBottomSheet: () -> Unit,
    onNavigateToRequiredUpdatePrompt: () -> Unit,
    onNavigateToUpdateInProgress: () -> Unit,
) {
    val context = LocalContext.current
    var showMobileKey by remember { mutableStateOf(false) }

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
                onClick = { onRefreshUser() }
            )
            if (BuildConfig.DEBUG) {
                SettingsMenuLink(
                    icon = { Icon(Icons.Outlined.VerifiedUser, contentDescription = "verified user") },
                    title = { Text(text = "DEBUG: Recognized permissions") },
                    subtitle = { Text(text = user?.allPermissions?.joinToString(separator = ", ") ?: "None") },
                    onClick = { onRefreshUser() }
                )
                SettingsMenuLink(
                    icon = { Icon(Icons.Outlined.Update, contentDescription = "update") },
                    title = { Text(text = "DEBUG: Open optional update bottom sheet") },
                    onClick = { onNavigateToOptionalUpdateBottomSheet() }
                )
                SettingsMenuLink(
                    icon = { Icon(Icons.Outlined.Update, contentDescription = "update") },
                    title = { Text(text = "DEBUG: Open required update prompt") },
                    onClick = { onNavigateToRequiredUpdatePrompt() }
                )
                SettingsMenuLink(
                    icon = { Icon(Icons.Outlined.Update, contentDescription = "update") },
                    title = { Text(text = "DEBUG: Open update in progress screen") },
                    onClick = { onNavigateToUpdateInProgress() }
                )
            }
            SettingsMenuLink(
                icon = { Icon(Icons.Outlined.Logout, contentDescription = "logout") },
                title = { Text(text = "Logout") },
                onClick = onLogout
            )
            SettingsHeader("Mobile Credential POC")
            Text(
                text = "Encryption key for reading mobile BuzzCards (insecure; development only).",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            OutlinedTextField(
                value = mobileCredentialKeyDraft,
                onValueChange = onMobileCredentialKeyDraftChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                label = { Text("AES Key (32 hex characters)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                visualTransformation = if (showMobileKey) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { showMobileKey = !showMobileKey }) {
                        Icon(
                            if (showMobileKey) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (showMobileKey) "Hide key" else "Show key",
                        )
                    }
                },
            )
            mobileCredentialKeySaveError?.let {
                Text(
                    text = it,
                    color = danger,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                Button(onClick = onSaveMobileCredentialKey) {
                    Text("Save key")
                }
                TextButton(onClick = onClearMobileCredentialKey) {
                    Text("Clear")
                }
            }
            SettingsHeader("About")
            SettingsMenuLink(
                icon = { Icon(Icons.Outlined.Home, contentDescription = "home") },
                title = { Text(text = "Server") },
                subtitle = {
                    Text(
                    text = "${appEnv.name} (${appEnv.apiBaseUrl})"
                )
                },
                onClick = {}
            )
            SettingsMenuLink(
                icon = { Icon(Icons.Outlined.Build, contentDescription = "build") },
                title = { Text(text = "Version") },
                subtitle = {
                    Text(
                    text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                )
                },
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
    val mobileCredentialKeyDraft by viewModel.mobileCredentialKeyDraft.collectAsState()
    val mobileCredentialKeySaveError by viewModel.mobileCredentialKeySaveError.collectAsState()
    val secondaryThemeColor = MaterialTheme.colorScheme.background
    ContentPadding {
       Settings(
           appEnv = viewModel.globalSettings.appEnv,
           user = state.user,
           mobileCredentialKeyDraft = mobileCredentialKeyDraft,
           mobileCredentialKeySaveError = mobileCredentialKeySaveError,
           onMobileCredentialKeyDraftChange = { viewModel.setMobileCredentialKeyDraft(it) },
           onSaveMobileCredentialKey = { viewModel.saveMobileCredentialKey() },
           onClearMobileCredentialKey = { viewModel.clearMobileCredentialKey() },
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
        mobileCredentialKeyDraft = "",
        mobileCredentialKeySaveError = null,
        onMobileCredentialKeyDraftChange = {},
        onSaveMobileCredentialKey = {},
        onClearMobileCredentialKey = {},
        onLogout = {},
        onOpenPrivacyPolicy = {},
        onOpenMakeAWish = {},
        onRefreshUser = {},
        onNavigateToOptionalUpdateBottomSheet = {},
        onNavigateToRequiredUpdatePrompt = {},
        onNavigateToUpdateInProgress = {},
    )
}

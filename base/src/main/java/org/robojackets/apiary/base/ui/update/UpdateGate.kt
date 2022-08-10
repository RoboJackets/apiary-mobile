package org.robojackets.apiary.base.ui.update

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.clientVersionStalenessDays
import com.google.android.play.core.ktx.updatePriority
import kotlinx.coroutines.launch
import se.warting.inappupdate.compose.rememberInAppUpdateState
import timber.log.Timber

fun isUpdateRequired(appUpdateInfo: AppUpdateInfo): Boolean {
    Timber.i("isUpdateRequired?")
    Timber.i("Available version: ${appUpdateInfo.availableVersionCode()}")
    Timber.i("Priority: ${appUpdateInfo.updatePriority()}")
    Timber.i("Staleness: ${appUpdateInfo.clientVersionStalenessDays()}")

    return false
}

fun isImmediateUpdateOptional(priority: Int, staleness: Int): Boolean {
    return when (priority) {
        0, 1, 2 -> staleness >= 14
        3 -> staleness >= 4
        4 -> true
        5 -> true
        else -> {
            Timber.w("Unknown priority $priority when evaluating for flexible update")
            false
        }
    }
}

fun isImmediateUpdateRequired(priority: Int, staleness: Int): Boolean {
    return when (priority) {
        0, 1, 2 -> staleness >= 21
        3 -> staleness >= 21
        4 -> staleness >= 1
        5 -> true
        else -> {
            Timber.w("Unknown priority $priority when evaluating for immediate update")
            false
        }
    }
}

// https://stackoverflow.com/a/68423182
fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

const val UPDATE_REQUEST_CODE = 1999

@Composable
fun UpdateGate(
    navReady: Boolean,
    onShowOptionalSheet: () -> Unit,
    content: @Composable () -> Unit,
) {
    val updateState = rememberInAppUpdateState()
    val scope = rememberCoroutineScope()

    when (val result = updateState.appUpdateResult) {
        is AppUpdateResult.NotAvailable -> RequiredUpdatePrompt()
        is AppUpdateResult.Available -> {
            val priority = result.updateInfo.updatePriority
            val staleness = result.updateInfo.clientVersionStalenessDays ?: -1

            val immediateAllowed =
                result.updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

            val immediateRequired =
                immediateAllowed && isImmediateUpdateRequired(priority, staleness)
            val immediateOptional =
                immediateAllowed && isImmediateUpdateOptional(priority, staleness)

            when {
                immediateRequired -> RequiredUpdatePrompt()
                immediateOptional -> {
                    LaunchedEffect(navReady) {
                        if (navReady) {
                            onShowOptionalSheet()
                        }
                    }
                }
                else -> content()
            }
        }
        // TODO: Implement a nice fullscreen UI for these two states
        is AppUpdateResult.InProgress -> Text("An app update is in progress. Please wait...")
        is AppUpdateResult.Downloaded -> {
            Text("An app update has been downloaded and is ready to install. Please wait...")
            LaunchedEffect(result) {
                scope.launch {
                    result.completeUpdate()
                }
            }
        }
    }
}

@Composable
fun UpdateStatus() {
    val updateState = rememberInAppUpdateState()

    when (val result = updateState.appUpdateResult) {
        is AppUpdateResult.NotAvailable -> Text("Up to date")
        is AppUpdateResult.Available -> {
            val priority = result.updateInfo.updatePriority
            val staleness = result.updateInfo.clientVersionStalenessDays ?: -1
            if (isUpdateRequired(result.updateInfo)) Text("Optional update available, priority: $priority, staleness: $staleness") else Text(
                "Required update ready, priority: $priority, staleness: $staleness")
        }
        is AppUpdateResult.InProgress -> Text("In progress")
        is AppUpdateResult.Downloaded -> Text("Downloaded")
    }
}

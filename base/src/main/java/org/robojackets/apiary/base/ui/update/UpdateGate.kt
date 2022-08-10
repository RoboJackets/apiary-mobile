package org.robojackets.apiary.base.ui.update

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.clientVersionStalenessDays
import com.google.android.play.core.ktx.updatePriority
import kotlinx.coroutines.launch
import se.warting.inappupdate.compose.rememberInAppUpdateState
import timber.log.Timber

const val UPDATE_PRIORITY_LOWEST = 0
const val UPDATE_PRIORITY_LOWER = 1
const val UPDATE_PRIORITY_LOW = 2
const val UPDATE_PRIORITY_MEDIUM = 3
const val UPDATE_PRIORITY_HIGH = 4
const val UPDATE_PRIORITY_HIGHEST = 5

const val OPT_UPDATE_STALENESS_THRESHOLD_LOW_PRIORITY = 14
const val OPT_UPDATE_STALENESS_THRESHOLD_MEDIUM_PRIORITY = 4

fun isImmediateUpdateOptional(priority: Int, staleness: Int): Boolean {
    return when (priority) {
        UPDATE_PRIORITY_LOWEST,
        UPDATE_PRIORITY_LOWER,
        UPDATE_PRIORITY_LOW -> staleness >= OPT_UPDATE_STALENESS_THRESHOLD_LOW_PRIORITY
        UPDATE_PRIORITY_MEDIUM -> staleness >= OPT_UPDATE_STALENESS_THRESHOLD_MEDIUM_PRIORITY
        UPDATE_PRIORITY_HIGH -> true
        UPDATE_PRIORITY_HIGHEST -> true
        else -> {
            Timber.w("Unknown priority $priority when evaluating for flexible update")
            false
        }
    }
}

const val REQ_UPDATE_STALENESS_THRESHOLD_LOW_PRIORITY = 21
const val REQ_UPDATE_STALENESS_THRESHOLD_MEDIUM_PRIORITY = 21
const val REQ_UPDATE_STALENESS_THRESHOLLD_HIGH_PRIORITY = 1

fun isImmediateUpdateRequired(priority: Int, staleness: Int): Boolean {
    return when (priority) {
        UPDATE_PRIORITY_LOWEST,
        UPDATE_PRIORITY_LOWER,
        UPDATE_PRIORITY_LOW -> staleness >= REQ_UPDATE_STALENESS_THRESHOLD_LOW_PRIORITY
        UPDATE_PRIORITY_MEDIUM -> staleness >= REQ_UPDATE_STALENESS_THRESHOLD_MEDIUM_PRIORITY
        UPDATE_PRIORITY_HIGH -> staleness >= REQ_UPDATE_STALENESS_THRESHOLLD_HIGH_PRIORITY
        UPDATE_PRIORITY_HIGHEST -> true
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

@Composable
fun UpdateGate(
    navReady: Boolean,
    onShowOptionalSheet: () -> Unit,
    content: @Composable () -> Unit,
) {
    val updateState = rememberInAppUpdateState()
    val scope = rememberCoroutineScope()

    when (val result = updateState.appUpdateResult) {
        is AppUpdateResult.NotAvailable -> content()
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
                    content() // If you leave off content here, the app will crash because this is
                              // a bottom sheet
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

            val immediateAllowed =
                result.updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

            val immediateRequired =
                immediateAllowed && isImmediateUpdateRequired(priority, staleness)
            val immediateOptional =
                immediateAllowed && isImmediateUpdateOptional(priority, staleness)

            when {
                immediateRequired -> Text("Required update available (priority: " +
                        "${priority}, staleness: ${staleness})")
                immediateOptional -> Text("Update available (priority: " +
                        "${priority}, staleness: ${staleness})")
                else -> Text("Up to date")
            }
        }
        is AppUpdateResult.InProgress -> Text("Update in progress")
        is AppUpdateResult.Downloaded -> Text("Update downloaded")
    }
}

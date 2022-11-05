package org.robojackets.apiary.ui.update

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.clientVersionStalenessDays
import com.google.android.play.core.ktx.updatePriority
import kotlinx.coroutines.launch
import org.robojackets.apiary.base.ui.util.OnLifecycleEvent
import org.robojackets.apiary.base.ui.util.getActivity
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
const val REQ_UPDATE_STALENESS_THRESHOLD_HIGH_PRIORITY = 1

fun isImmediateUpdateRequired(priority: Int, staleness: Int): Boolean {
    return when (priority) {
        UPDATE_PRIORITY_LOWEST,
        UPDATE_PRIORITY_LOWER,
        UPDATE_PRIORITY_LOW -> staleness >= REQ_UPDATE_STALENESS_THRESHOLD_LOW_PRIORITY
        UPDATE_PRIORITY_MEDIUM -> staleness >= REQ_UPDATE_STALENESS_THRESHOLD_MEDIUM_PRIORITY
        UPDATE_PRIORITY_HIGH -> staleness >= REQ_UPDATE_STALENESS_THRESHOLD_HIGH_PRIORITY
        UPDATE_PRIORITY_HIGHEST -> true
        else -> {
            Timber.w("Unknown priority $priority when evaluating for immediate update")
            false
        }
    }
}

@Suppress("ComplexMethod")
@Composable
fun UpdateGate(
    navReady: Boolean,
    onShowRequiredUpdatePrompt: () -> Unit,
    onShowOptionalUpdatePrompt: () -> Unit,
    onShowUpdateInProgressScreen: () -> Unit,
    content: @Composable () -> Unit,
) {
    content()
    val updateState = rememberInAppUpdateState()
    val scope = rememberCoroutineScope()
    val result = updateState.appUpdateResult
    val context = LocalContext.current
    OnLifecycleEvent { owner, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (result is AppUpdateResult.Available) {
                    if (result.updateInfo.updateAvailability()
                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        context.getActivity()
                            ?.let { result.startImmediateUpdate(it, UPDATE_REQUEST_CODE) }
                    }
                }
            }
            else -> Unit
        }
    }

    when (result) {
        is AppUpdateResult.NotAvailable -> Unit
        is AppUpdateResult.Available -> {
            val priority = result.updateInfo.updatePriority
            val staleness = result.updateInfo.clientVersionStalenessDays ?: -1

            val immediateAllowed =
                result.updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

            val immediateRequired = immediateAllowed &&
                    isImmediateUpdateRequired(priority, staleness)
            val immediateOptional = immediateAllowed &&
                    !immediateRequired &&
                    isImmediateUpdateOptional(priority, staleness)

            when {
                immediateRequired -> {
                    LaunchedEffect(navReady) {
                        if (navReady) {
                            onShowRequiredUpdatePrompt()
                        }
                    }
                }
                immediateOptional -> {
                    LaunchedEffect(navReady) {
                        if (navReady) {
                            onShowOptionalUpdatePrompt()
                        }
                    }
                }
                else -> {
                    Timber.d("An update is available but no action is required currently")
                    content()
                }
            }
        }
        is AppUpdateResult.InProgress -> Unit
        is AppUpdateResult.Downloaded -> {
            LaunchedEffect(result) {
                if (navReady) {
                    onShowUpdateInProgressScreen()
                    scope.launch {
                        result.completeUpdate()
                    }
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

            val immediateRequired = immediateAllowed &&
                    isImmediateUpdateRequired(priority, staleness)
            val immediateOptional = immediateAllowed &&
                    !immediateRequired &&
                    isImmediateUpdateOptional(priority, staleness)

            when {
                immediateRequired -> Text("Required update available (priority: " +
                        "$priority, staleness: $staleness)")
                immediateOptional -> Text("Optional update available (priority: " +
                        "$priority, staleness: $staleness)")
                else -> Text("Available")
            }
        }
        is AppUpdateResult.InProgress -> Text("Update in progress")
        is AppUpdateResult.Downloaded -> Text("Update downloaded")
    }
}

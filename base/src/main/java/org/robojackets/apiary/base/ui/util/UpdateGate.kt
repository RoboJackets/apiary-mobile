package org.robojackets.apiary.base.ui.util

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.*
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

fun isFlexibleUpdateRequired(priority: Int, staleness: Int): Boolean {
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

@Composable
fun UpdateGate(content: @Composable () -> Unit) {
    val updateState = rememberInAppUpdateState()
    val scope = rememberCoroutineScope()
    val ignoreUpdate by remember { mutableStateOf(false) }
    Timber.i("UpdateGate!")
    Timber.i(updateState.appUpdateResult.toString())

    if (ignoreUpdate) {
        content()
    } else {
        when (val result = updateState.appUpdateResult) {
            is AppUpdateResult.NotAvailable -> content()
            is AppUpdateResult.Available -> {
                val priority = result.updateInfo.updatePriority
                val staleness = result.updateInfo.clientVersionStalenessDays ?: 0

                val immediateAllowed =
                    result.updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                val flexibleAllowed = result.updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)

                // Fallback to flexible if an immediate update is desired but not allowed
                val immediateRequired =
                    immediateAllowed && isImmediateUpdateRequired(priority, staleness)
                val flexibleRequired =
                    flexibleAllowed && (immediateRequired || isFlexibleUpdateRequired(priority,
                        staleness))

                Text(text = "Immediate: $immediateAllowed, flexible: $flexibleAllowed")

                when {
                    immediateRequired -> {
                        Text("Time for an immediate update")
                    }
                    flexibleRequired -> {
                        ContentPadding {
                            Column {
                                Text("A flexible update is available. Do you want to install it now?")
                            }
                        }

                    }
                    else -> content()
                }
            }
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
}
@Composable
fun UpdateStatus() {
    val updateState = rememberInAppUpdateState()

    when (val result = updateState.appUpdateResult) {
        is AppUpdateResult.NotAvailable -> Text("Not available")
        is AppUpdateResult.Available -> {
            if (isUpdateRequired(result.updateInfo)) Text("Available and required")  else Text("Available but not required")
        }
        is AppUpdateResult.InProgress -> Text("In progress")
        is AppUpdateResult.Downloaded -> Text("Downloaded")
    }
}
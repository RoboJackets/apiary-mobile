package org.robojackets.apiary.base.ui.util

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.clientVersionStalenessDays
import com.google.android.play.core.ktx.updatePriority
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

    Timber.i("UpdateGate!")
    Timber.i(updateState.appUpdateResult.toString())
    when (val result = updateState.appUpdateResult) {
        is AppUpdateResult.NotAvailable -> content()
        is AppUpdateResult.Available -> {
            val priority = result.updateInfo.updatePriority
            val staleness = result.updateInfo.clientVersionStalenessDays ?: 0
            if (isImmediateUpdateRequired(priority, staleness)) {
                Text("Immediate update proposed")
            } else if (isFlexibleUpdateRequired(priority, staleness)) {
                Text("Flexible update proposed")
            } else {
                content()
            }
        }
        is AppUpdateResult.InProgress -> content()
        is AppUpdateResult.Downloaded -> content()
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
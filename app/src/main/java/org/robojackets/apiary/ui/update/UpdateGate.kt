package org.robojackets.apiary.ui.update

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import se.warting.inappupdate.compose.InAppUpdateState
import se.warting.inappupdate.compose.rememberInAppUpdateState
import timber.log.Timber

@Composable
fun rememberInAppUpdateStateWithDefaults(): InAppUpdateState {
    return rememberInAppUpdateState(
        highPrioritizeUpdates = 5,
        mediumPrioritizeUpdates = 3,
        promptIntervalHighPrioritizeUpdateInDays = 1,
        promptIntervalMediumPrioritizeUpdateInDays = 4,
        promptIntervalLowPrioritizeUpdateInDays = 8,
    )
}

@Suppress("ComplexMethod", "LongMethod")
@Composable
fun UpdateGate(
    navReady: Boolean,
    onShowRequiredUpdatePrompt: () -> Unit,
    onShowOptionalUpdatePrompt: () -> Unit,
    onShowUpdateInProgressScreen: () -> Unit,
    content: @Composable () -> Unit,
) {
    content()
    when (val inAppUpdateState = rememberInAppUpdateStateWithDefaults()) {
        is InAppUpdateState.DownloadedUpdate -> {
            if (inAppUpdateState.isRequiredUpdate) {
                LaunchedEffect(navReady) {
                    if (navReady) {
                        onShowRequiredUpdatePrompt()
                    }
                }
            } else {
                LaunchedEffect(navReady) {
                    if (navReady) {
                        onShowOptionalUpdatePrompt()
                    }
                }
            }
            content()
        }

        is InAppUpdateState.InProgressUpdate -> {
            Timber.i("Update in progress: ${inAppUpdateState.installState.bytesDownloaded}")
            LaunchedEffect(navReady) {
                if (navReady) {
                    onShowUpdateInProgressScreen()
                }
            }
            UpdateInProgress()
        }

        InAppUpdateState.Loading -> {
            Timber.i("In-app update - loading state")
            content()
        }
        InAppUpdateState.NotAvailable -> {
            content()
        }
        is InAppUpdateState.OptionalUpdate -> {
            LaunchedEffect(navReady) {
                if (navReady && inAppUpdateState.shouldPrompt) {
                    onShowOptionalUpdatePrompt()
                }
            }
            content()
        }

        is InAppUpdateState.RequiredUpdate -> {
            LaunchedEffect(navReady) {
                if (navReady && inAppUpdateState.shouldPrompt) {
                    onShowRequiredUpdatePrompt()
                }
            }
            content()
        }

        is InAppUpdateState.Error -> {
            content()
        }
    }
}

@Composable
fun UpdateStatus() {
    when (val inAppUpdateState = rememberInAppUpdateStateWithDefaults()) {
        is InAppUpdateState.DownloadedUpdate -> {
            if (inAppUpdateState.isRequiredUpdate) {
                Text("Downloaded > required")
            } else {
                Text("Downloaded > optional")
            }
        }

        is InAppUpdateState.InProgressUpdate -> {
            Text(
                "In progress (${inAppUpdateState.installState.bytesDownloaded}/" +
                    "${inAppUpdateState.installState.totalBytesToDownload}) bytes) " +
                    "| Install status: ${inAppUpdateState.installState.installStatus} " +
                    "| Error code: ${inAppUpdateState.installState.installErrorCode} " +
                    "| Package name: ${inAppUpdateState.installState.packageName}"
            )
        }

        InAppUpdateState.Loading -> {
            Text("Loading")
        }
        InAppUpdateState.NotAvailable -> {
            Text("Not available")
        }
        is InAppUpdateState.OptionalUpdate -> {
            Text(
                "Optional update | Should prompt: ${inAppUpdateState.shouldPrompt} " +
                    "| Priority: ${inAppUpdateState.appUpdateInfo.priority}" +
                    "| Staleness: ${inAppUpdateState.appUpdateInfo.staleDays}" +
                    "| Version code: ${inAppUpdateState.appUpdateInfo.versionCode}"
            )
        }

        is InAppUpdateState.RequiredUpdate -> {
            Text(
                "Required update | Should prompt: ${inAppUpdateState.shouldPrompt} " +
                    "| Priority: ${inAppUpdateState.appUpdateInfo.priority}" +
                    "| Staleness: ${inAppUpdateState.appUpdateInfo.staleDays}" +
                    "| Version code: ${inAppUpdateState.appUpdateInfo.versionCode}"
            )
        }

        is InAppUpdateState.Error -> {
            Text("Error: ${inAppUpdateState.exception}")
        }
    }
}

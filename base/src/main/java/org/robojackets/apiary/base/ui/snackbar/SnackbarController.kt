package org.robojackets.apiary.base.ui.snackbar

/**
 * This file is from https://gist.github.com/krizzu/60b7ea7e7865e6495cbd9359f20c4b91
 * See also https://www.kborowy.com/blog/easy-compose-snackbar/
 */

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.EmptyCoroutineContext

private val LocalSnackbarController = staticCompositionLocalOf {
    SnackbarController(
        host = SnackbarHostState(),
        scope = CoroutineScope(EmptyCoroutineContext)
    )
}
private val channel = Channel<SnackbarChannelMessage>(capacity = Int.MAX_VALUE)

@Composable
fun SnackbarControllerProvider(content: @Composable (snackbarHost: SnackbarHostState) -> Unit) {
    val snackHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val snackController = remember(scope) { SnackbarController(snackHostState, scope) }

    DisposableEffect(snackController, scope) {
        val job = scope.launch {
            for (payload in channel) {
                Timber.d("Snackbar message: $payload")
                snackController.showMessage(
                    message = payload.message,
                    duration = payload.duration,
                    action = payload.action
                )
            }
        }

        onDispose {
            Timber.d("Snackbar job was disposed")
            job.cancel()
        }
    }

    CompositionLocalProvider(LocalSnackbarController provides snackController) {
        content(
            snackHostState
        )
    }
}

@Immutable
class SnackbarController(
    private val host: SnackbarHostState,
    private val scope: CoroutineScope,
) {
    companion object {
        val current
            @Composable
            @ReadOnlyComposable
            get() = LocalSnackbarController.current

        fun showMessage(
            message: String,
            action: SnackbarAction? = null,
            duration: SnackbarDuration = SnackbarDuration.Short,
        ) {
            Timber.d("snackbar showMessage")
            if (channel.isClosedForSend) {
                throw IllegalStateException("snackbar channel is closed")
            }

            val result = channel.trySend(
                SnackbarChannelMessage(
                    message = message,
                    duration = duration,
                    action = action
                )
            )
            Timber.d("Snackbar message sent: success? ${result.isSuccess}")
        }
    }

    fun showMessage(
        message: String,
        action: SnackbarAction? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ) {
        Timber.d("snackbar show message with scope")
        scope.launch {
            Timber.d("snackbar show message with scope: message is $message")
            /**
             * note: uncomment this line if you want snackbar to be displayed immediately,
             * rather than being enqueued and waiting [duration] * current_queue_size
             */
            Timber.d(host.currentSnackbarData.toString())
//            host.currentSnackbarData?.dismiss()
            val result =
                host.showSnackbar(
                    message = message,
                    actionLabel = action?.title,
                    duration = duration
                )

            Timber.d("snackbar with scope: result is $result")
            if (result == SnackbarResult.ActionPerformed) {
                action?.onActionPress?.invoke()
            }
        }
    }
}

data class SnackbarChannelMessage(
    val message: String,
    val action: SnackbarAction?,
    val duration: SnackbarDuration = SnackbarDuration.Short,
)

data class SnackbarAction(val title: String, val onActionPress: () -> Unit)

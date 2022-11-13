package org.robojackets.apiary

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.sentry.SentryLevel
import io.sentry.android.core.SentryAndroid
import io.sentry.android.timber.SentryTimberIntegration
import timber.log.Timber

// Note: this class has to be in the same module as the @AndroidEntryPoint annotated class, which
// is MainActivity.  In other words, you can't move this class to another module to solve
// dependency issues (use dependency injection instead!).
@HiltAndroidApp
class ApiaryMobileApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val isDebugBuild = BuildConfig.DEBUG

        if (isDebugBuild) {
            Timber.plant(Timber.DebugTree())
        }

        SentryAndroid.init(this) { options ->
            options.setBeforeSend { sentryEvent, _ ->
                when (isDebugBuild) {
                    true -> null
                    else -> sentryEvent
                }
            }
            options.addIntegration(
                SentryTimberIntegration(
                    minEventLevel = SentryLevel.WARNING,
                    minBreadcrumbLevel = SentryLevel.INFO,
                )
            )
        }

    }
}

package org.robojackets.apiary.base

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import hu.autsoft.krate.SimpleKrate
import hu.autsoft.krate.stringPref
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalSettings @Inject constructor(
    @ApplicationContext context: Context
) : SimpleKrate(context) {
    var appEnvName by stringPref("APP_ENV_NAME", AppEnvironment.Production.name)
    var accessToken by stringPref("AUTH_TOKEN", "")
    var refreshToken by stringPref("REFRESH_TOKEN", "")

    val appEnv: AppEnvironment
        get() = AppEnvironment.valueOf(appEnvName)

    fun clearLoginInfo() {
        accessToken = ""
        refreshToken = ""
    }
}

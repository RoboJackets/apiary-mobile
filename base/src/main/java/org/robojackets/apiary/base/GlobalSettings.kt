package org.robojackets.apiary.base

import android.content.Context
import hu.autsoft.krate.SimpleKrate
import hu.autsoft.krate.stringPref

class GlobalSettings(context: Context) : SimpleKrate(context) {
    companion object {
        const val DEFAULT_API_BASE_URL = "https://apiary-test.robojackets.org"
    }

    var apiBaseUrl by stringPref("API_BASE_URL", DEFAULT_API_BASE_URL)
}


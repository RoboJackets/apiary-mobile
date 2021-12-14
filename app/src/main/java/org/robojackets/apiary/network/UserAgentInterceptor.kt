package org.robojackets.apiary.network

import android.content.Context
import android.os.Build
import okhttp3.Interceptor
import okhttp3.Response
import org.robojackets.apiary.BuildConfig
import okhttp3.internal.userAgent as okHttpVersion

// Based on https://medium.com/mobile-app-development-publication/setting-useragent-for-android-network-9daf5264ef3f
class UserAgentInterceptor(context: Context) : Interceptor {
    companion object {
        private const val USER_AGENT = "User-Agent"
    }

    private val userAgent = "${getApplicationName(context)}/" +
            "${BuildConfig.VERSION_NAME} " +
            "(${context.packageName}; " +
            "build:${BuildConfig.VERSION_CODE} " +
            "Android SDK ${Build.VERSION.SDK_INT}) " +
            okHttpVersion + " " +
            getDeviceName()

    private fun getApplicationName(context: Context): String {
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
            stringId
        )
    }

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model.replaceFirstChar { it.titlecase() }
        } else manufacturer.replaceFirstChar { it.titlecase() } + " " + model
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header(USER_AGENT, userAgent)
            .build()

        return chain.proceed(request)
    }
}

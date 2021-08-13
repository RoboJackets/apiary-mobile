package org.robojackets.apiary.auth.network

import okhttp3.Interceptor
import okhttp3.Response
import org.robojackets.apiary.auth.AuthStateManager

class AuthHeaderInterceptor constructor(
    val authStateManager: AuthStateManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = authStateManager.current.accessToken
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(request)
    }

}
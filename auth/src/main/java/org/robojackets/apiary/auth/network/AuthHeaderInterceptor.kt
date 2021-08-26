package org.robojackets.apiary.auth.network

import android.util.Log
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthorizationService
import okhttp3.Interceptor
import okhttp3.Response
import org.robojackets.apiary.auth.AuthStateManager
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthHeaderInterceptor constructor(
    val authStateManager: AuthStateManager,
    val authService: AuthorizationService,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        runBlocking {
            suspendCoroutine<Unit> { continuation ->
                authStateManager.current.performActionWithFreshTokens(
                    authService
                ) { accessToken, _, ex ->
                    Log.d("AuthHeaderInterceptor", "the new access token has a length of ${accessToken?.length}")

                    if (ex != null) {
                        Log.e("AuthHeaderInterceptor", "Exception while getting trying to get new access token", ex)
                    }

                    requestBuilder.addHeader("Authorization", "Bearer $accessToken")
                    continuation.resume(Unit)
                }
            }
        }

        requestBuilder.header("Accept", "application/json")
        return chain.proceed(requestBuilder.build())
    }
}
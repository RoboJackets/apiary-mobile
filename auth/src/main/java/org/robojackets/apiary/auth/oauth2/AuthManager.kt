package org.robojackets.apiary.auth.oauth2

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import org.robojackets.apiary.base.GlobalSettings
import javax.inject.Inject

@ViewModelScoped
class AuthManager @Inject constructor(
    @ApplicationContext context: Context,
    val authService: AuthorizationService
) {
    val settings = GlobalSettings(context)
    private val redirectUri = Uri.parse("org.robojackets.apiary://oauth")

    fun getAuthRequest(): AuthorizationRequest {
        val clientId = settings.appEnv.clientId

        val authorizationEndpoint = Uri.parse("${settings.appEnv.apiBaseUrl}/oauth/authorize")
        val tokenEndpoint: Uri = Uri.parse("${settings.appEnv.apiBaseUrl}/oauth/token")

        val serviceConfig = AuthorizationServiceConfiguration(authorizationEndpoint, tokenEndpoint)
        return AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            redirectUri
        ).build()
    }
}

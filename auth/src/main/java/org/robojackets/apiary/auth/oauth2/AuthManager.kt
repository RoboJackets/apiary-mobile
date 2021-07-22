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
    @ApplicationContext context: Context
) {
    private var REDIRECT_URI: Uri
    var serviceConfig: AuthorizationServiceConfiguration
    var authService: AuthorizationService
    val settings = GlobalSettings(context)
    private val CLIENT_ID = settings.appEnv.clientId

    init {
        val AUTHORIZATION_ENDPOINT =
            Uri.parse("${settings.appEnv.apiBaseUrl}/oauth/authorize")
        val TOKEN_ENDPOINT = Uri.parse("${settings.appEnv.apiBaseUrl}/oauth/token")

        REDIRECT_URI = Uri.parse("org.robojackets.apiary://oauth")
        serviceConfig = AuthorizationServiceConfiguration(AUTHORIZATION_ENDPOINT, TOKEN_ENDPOINT)
        authService = AuthorizationService(context)
    }

    val authRequest = AuthorizationRequest.Builder(
        serviceConfig,
        CLIENT_ID,
        ResponseTypeValues.CODE,
        REDIRECT_URI
    ).build()
}

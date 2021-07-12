package org.robojackets.apiary.auth.oauth2

import android.content.Context
import android.net.Uri
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

class AuthManager(context: Context) {
    private val CLIENT_ID = "93c98c66-dffa-4ad2-bcd7-fed4f767e08e" // FIXME DO NOT COMMIT
    private var REDIRECT_URI: Uri
    var serviceConfig: AuthorizationServiceConfiguration
    var authService: AuthorizationService

    init {
        val AUTHORIZATION_ENDPOINT =
            Uri.parse("https://apiary-test.robojackets.org/oauth/authorize")
        val TOKEN_ENDPOINT = Uri.parse("https://apiary-test.robojackets.org/oauth/token")

        REDIRECT_URI = Uri.parse("org.robojackets.apiary.android://oauth")
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
package org.robojackets.apiary.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import net.openid.appauth.AuthorizationService
import org.robojackets.apiary.auth.oauth2.AuthManager

@Module
@InstallIn(ActivityComponent::class)
class MainActivityModule {
    @Provides
    fun providesAuthManager(
        @ActivityContext context: Context,
        authorizationService: AuthorizationService
    ) = AuthManager(context, authorizationService)

    @Provides
    fun providesAuthService(
        @ActivityContext context: Context
    ) = AuthorizationService(context)
}

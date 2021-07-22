package org.robojackets.apiary.auth.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.robojackets.apiary.auth.oauth2.AuthManager

@Module
@InstallIn(ViewModelComponent::class)
class AuthModule {
    @Provides
    fun providesAuthManager(
        @ApplicationContext context: Context
    ) = AuthManager(context)
}
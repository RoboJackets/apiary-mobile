package org.robojackets.apiary.di

import android.content.Context
import com.nxp.nfclib.NxpNfcLib
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.robojackets.apiary.ApiaryMobileApplication
import org.robojackets.apiary.base.GlobalSettings
import org.robojackets.apiary.navigation.NavigationManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun providesNavigationManager() = NavigationManager()

    @Singleton
    @Provides
    fun providesApiaryMobileApplication() = ApiaryMobileApplication()

    @Singleton
    @Provides
    fun providesGlobalSettings(
        @ApplicationContext context: Context
    ) = GlobalSettings(context)

    @Singleton
    @Provides
    fun providesNxpNfcLib() = NxpNfcLib.getInstance()
}

package org.robojackets.apiary.di

import android.content.Context
import com.nxp.nfclib.NxpNfcLib
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import org.robojackets.apiary.ApiaryMobileApplication
import org.robojackets.apiary.BuildConfig
import org.robojackets.apiary.auth.AuthStateManager
import org.robojackets.apiary.base.GlobalSettings
import org.robojackets.apiary.base.repository.ServerInfoRepository
import org.robojackets.apiary.base.service.ServerInfoApiService
import org.robojackets.apiary.navigation.NavigationManager
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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
    fun providesAuthStateManager(
        @ApplicationContext context: Context
    ) = AuthStateManager.getInstance(context)

    @Singleton
    @Provides
    fun providesNxpNfcLib(): NxpNfcLib = NxpNfcLib.getInstance()

    @Singleton
    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(if (BuildConfig.DEBUG) BODY else BASIC) // Only log detailed
        // network requests in debug builds
        loggingInterceptor.redactHeader("Authorization") // Redact access tokens in headers

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun providesRetrofit(
        globalSettings: GlobalSettings,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(globalSettings.appEnv.apiBaseUrl.toString())
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun providesServerInfoApiService(
        retrofit: Retrofit
    ): ServerInfoApiService = retrofit.create(ServerInfoApiService::class.java)

    @Singleton
    @Provides
    fun provideServerInfoRepository(
        serverInfoApiService: ServerInfoApiService
    ) = ServerInfoRepository(serverInfoApiService)
}

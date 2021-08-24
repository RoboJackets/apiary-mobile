package org.robojackets.apiary.di

import android.content.Context
import com.nxp.nfclib.NxpNfcLib
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.AuthorizationService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.robojackets.apiary.BuildConfig
import org.robojackets.apiary.auth.AuthStateManager
import org.robojackets.apiary.auth.network.AuthHeaderInterceptor
import org.robojackets.apiary.auth.network.UserApiService
import org.robojackets.apiary.auth.oauth2.AuthManager
import org.robojackets.apiary.base.GlobalSettings
import org.robojackets.apiary.base.service.ServerInfoApiService
import org.robojackets.apiary.network.UserAgentInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(ActivityRetainedComponent::class)
object MainActivityModule {
    @Provides
    fun providesNxpNfcLib(): NxpNfcLib = NxpNfcLib.getInstance()

    @Provides
    fun providesAuthService(
        @ApplicationContext context: Context
    ) = AuthorizationService(context)

    @Provides
    fun providesOkHttpClient(
        @ApplicationContext context: Context,
        authStateManager: AuthStateManager,
        authManager: AuthManager,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC) // Only log detailed
        // network requests in debug builds
        loggingInterceptor.redactHeader("Authorization") // Redact access tokens in headers

        return OkHttpClient.Builder()
            .addInterceptor(UserAgentInterceptor(context))
            .addInterceptor(AuthHeaderInterceptor(authStateManager, authManager.authService))
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    fun providesRetrofit(
        globalSettings: GlobalSettings,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(globalSettings.appEnv.apiBaseUrl.toString())
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Provides
    fun providesServerInfoApiService(
        retrofit: Retrofit
    ) = retrofit.create(ServerInfoApiService::class.java)

    @Provides
    fun providesUserApiService(
        retrofit: Retrofit
    ) = retrofit.create(UserApiService::class.java)
}

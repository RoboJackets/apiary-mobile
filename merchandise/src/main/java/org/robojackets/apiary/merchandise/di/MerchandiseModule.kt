package org.robojackets.apiary.merchandise.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import org.robojackets.apiary.merchandise.network.MerchandiseApiService
import retrofit2.Retrofit

@Module
@InstallIn(ActivityRetainedComponent::class)
object MerchandiseModule {
    @Provides
    fun providesMerchandiseApiService(
        retrofit: Retrofit
    ): MerchandiseApiService = retrofit.create(MerchandiseApiService::class.java)
}
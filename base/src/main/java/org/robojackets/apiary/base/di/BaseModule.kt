package org.robojackets.apiary.base.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import org.robojackets.apiary.base.service.DeviceService
import org.robojackets.apiary.base.service.MeetingsService
import retrofit2.Retrofit

@Module
@InstallIn(ActivityRetainedComponent::class)
object BaseModule {
    @Provides
    fun providesMeetingsService(
        retrofit: Retrofit
    ): MeetingsService = retrofit.create(MeetingsService::class.java)

    @Provides
    fun providesDeviceService(
        retrofit: Retrofit
    ): DeviceService = retrofit.create(DeviceService::class.java)
}

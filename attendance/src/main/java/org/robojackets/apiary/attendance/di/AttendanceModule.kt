package org.robojackets.apiary.attendance.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import org.robojackets.apiary.attendance.network.AttendanceApiService
import retrofit2.Retrofit

@Module
@InstallIn(ActivityRetainedComponent::class)
object AttendanceModule {
    @Provides
    fun providesAttendanceApiService(
        retrofit: Retrofit
    ): AttendanceApiService = retrofit.create(AttendanceApiService::class.java)
}

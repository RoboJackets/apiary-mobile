package org.robojackets.apiary.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.robojackets.apiary.ApiaryMobileApplication
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providesApiaryMobileApplication() = ApiaryMobileApplication()
}

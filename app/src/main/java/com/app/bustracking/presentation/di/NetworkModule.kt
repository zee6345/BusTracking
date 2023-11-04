package com.app.bustracking.presentation.di

import com.app.bustracking.data.api.ApiClient
import com.app.bustracking.data.api.ApiService
import com.app.bustracking.data.remotesource.AppRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideClient(): ApiService {
        return ApiClient.createService().create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppRepo(apiService: ApiService): AppRepoImpl {
        return AppRepoImpl(apiService)
    }

}
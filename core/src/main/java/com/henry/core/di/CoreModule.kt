package com.henry.core.di

import com.henry.core.data.CoroutineDispatcherProvider
import com.henry.core.data.DefaultDispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    @Singleton
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcherProvider = DefaultDispatcherProvider()
}
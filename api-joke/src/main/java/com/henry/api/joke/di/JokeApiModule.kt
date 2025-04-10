package com.henry.api.joke.di

import com.henry.api.joke.data.api.JokeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit


@Module
@InstallIn(SingletonComponent::class)
object JokeApiModule {

    @Provides
    fun provideJokeApi(retrofit: Retrofit): JokeApi {
        return retrofit.create(JokeApi::class.java)
    }
}
package com.henry.api.joke.data.repository

import com.henry.api.joke.data.remote.JokeRemoteDataSource
import com.henry.core.data.CoroutineDispatcherProvider
import com.henry.core.data.resultFlow
import javax.inject.Inject

class JokeRepository @Inject constructor(
    private val remoteDataSource: JokeRemoteDataSource,
    private val dispatcherProvider: CoroutineDispatcherProvider
) {
    fun getCategories() = resultFlow(
        networkCall = { remoteDataSource.getCategories() },
        dispatcher = dispatcherProvider
    )

    fun getJokeByCategory(category: String) = resultFlow(
        networkCall = { remoteDataSource.getJokeByCategory(category) },
        dispatcher = dispatcherProvider
    )
}
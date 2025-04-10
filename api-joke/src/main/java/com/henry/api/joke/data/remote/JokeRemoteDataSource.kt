package com.henry.api.joke.data.remote

import com.henry.api.joke.data.api.JokeApi
import com.henry.core.base.BaseDataSource
import javax.inject.Inject

class JokeRemoteDataSource @Inject constructor(
    private val api: JokeApi
) : BaseDataSource() {

    suspend fun getCategories() = getResultWithSingleObject {
        api.getCategories()
    }

    suspend fun getJokeByCategory(category: String) = getResultWithSingleObject {
        api.getJoke(category)
    }
}
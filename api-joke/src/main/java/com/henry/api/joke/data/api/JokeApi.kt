package com.henry.api.joke.data.api

import com.henry.api.joke.util.JokeUrlConstants.GET_CATEGORIES
import com.henry.api.joke.util.JokeUrlConstants.GET_JOKE
import com.henry.api.joke.util.Path.CATEGORY
import com.henry.api.joke.util.Query.AMOUNT
import com.henry.api.joke.util.Query.TYPE
import com.henry.core.entity.category.JokeCategory
import com.henry.core.entity.jokeitem.JokeItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JokeApi {

    @GET(GET_CATEGORIES)
    suspend fun getCategories(): Response<JokeCategory>

    @GET(GET_JOKE)
    suspend fun getJoke(
        @Path(CATEGORY) category: String,
        @Query(TYPE) type: String = "single",
        @Query(AMOUNT) amount: Int = 2
    ): Response<JokeItem>
}
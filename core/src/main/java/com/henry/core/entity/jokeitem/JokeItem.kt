package com.henry.core.entity.jokeitem

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JokeItem (
    @field:Json(name = "error") val error: Boolean,
    @field:Json(name = "amount") val amount: Long,
    @field:Json(name = "jokes") val jokes: List<Joke>
)
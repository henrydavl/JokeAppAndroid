package com.henry.core.entity.jokeitem

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Joke (
    @field:Json(name = "category") val category: String,
    @field:Json(name = "type") val type: String,
    @field:Json(name = "joke") val joke: String,
    @field:Json(name = "flags") val flags: Flags,
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "safe") val safe: Boolean,
    @field:Json(name = "lang") val lang: String
)
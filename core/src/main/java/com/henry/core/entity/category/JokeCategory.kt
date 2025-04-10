package com.henry.core.entity.category

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JokeCategory (
    @field:Json(name = "error") val error: Boolean,
    @field:Json(name = "categories") val categories: List<String>,
    @field:Json(name = "categoryAliases") val categoryAliases: List<CategoryAlias>,
    @field:Json(name = "timestamp") val timestamp: Long
)
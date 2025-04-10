package com.henry.core.entity.category

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryAlias (
    @field:Json(name = "alias") val alias: String,
    @field:Json(name = "resolved") val resolved: String
)
package com.henry.core.entity.error

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @field:Json(name = "error") val error: Boolean,
    @field:Json(name = "internalError") val internalError: Boolean,
    @field:Json(name = "code") val code: Long,
    @field:Json(name = "message") val message: String,
    @field:Json(name = "causedBy") val causedBy: List<String>,
    @field:Json(name = "additionalInfo") val additionalInfo: String,
    @field:Json(name = "timestamp") val timestamp: Long
)
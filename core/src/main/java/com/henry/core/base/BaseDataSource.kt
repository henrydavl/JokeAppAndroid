package com.henry.core.base

import com.henry.core.entity.error.ErrorResponse
import com.henry.core.utils.Resource
import com.squareup.moshi.Moshi
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.net.SocketTimeoutException

abstract class BaseDataSource {
    suspend fun <T> getResultWithSingleObject(call: suspend () -> Response<T>): Resource<T> = try {
        val response = call()
        if (response.isSuccessful) response.body()?.run {
            Resource.Success(this)
        } ?: errorHandler(
            response.code(),
            response.errorBody(),
            "returns NULL. ${response.message()}"
        )
        else errorHandler(response.code(), response.errorBody(), "Error. ${response.message()}")
    } catch (ex: SocketTimeoutException) {
        errorHandler(504, null, "Timeout: ${ex.message ?: ex}")
    } catch (e: Exception) {
        Timber.e(e.localizedMessage)
        errorHandler(0, null, "Unexpected error: ${e.message ?: e}")
    }

    private fun <T> errorHandler(
        httpCode: Int,
        errorBody: ResponseBody?,
        extraMessage: String? = ""
    ): Resource<T> = try {
        val errorDetail = errorBody?.string()?.let {
            Moshi.Builder().build().adapter(ErrorResponse::class.java).fromJson(it)
        }
        Resource.Error(
            message = errorDetail?.message
                ?: "Network call has failed for the following reason(s): $extraMessage",
            data = null,
        )
    } catch (e: Exception) {
        Resource.Error(
            message = "Error parsing error body: $httpCode",
            data = null
        )
    }
}
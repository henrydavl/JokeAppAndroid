package com.henry.core.data

import com.henry.core.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

fun <A : Any> resultFlow(
    networkCall: suspend () -> Resource<A>,
    dispatcher: CoroutineDispatcherProvider = DefaultDispatcherProvider()
): Flow<Resource<A>> = flow {
    emit(Resource.Loading())

    when (val response = networkCall.invoke()) {
        is Resource.Success -> emit(Resource.Success(response.data))
        is Resource.Error -> emit(Resource.Error(message = response.message ?: "Unknown error"))
        is Resource.Loading -> Unit // Optional: ignore or log
    }
}.flowOn(dispatcher.io())


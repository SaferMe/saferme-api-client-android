package com.thundermaps.apilib.android.api.responses.models

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResultHandler @Inject constructor() {
    fun <T : Any> handleSuccess(data: T): Result<T> {
        return Result.Success(data)
    }

    fun <T : Any> handleException(exception: Exception): Result<T> {
        return Result.Error<T>(null, exception)
    }
}

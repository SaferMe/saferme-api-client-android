package com.thundermaps.apilib.android.api.responses.models

sealed class Result<out T : Any> {
    object Initial : Result<Nothing>()
    class Loading<T : Any>(val data: T?) : Result<T>()
    class Success<T : Any>(val data: T) : Result<T>()
    class Error<T : Any>(val data: T?, val exception: Exception) : Result<T>()

    val isLoading: Boolean
        get() = this is Loading

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    fun <X : Any> convert(conversion: (t: T) -> X): Result<X> {
        return when (this) {
            Initial -> Initial
            is Loading -> Loading(
                data = if (data != null) conversion(
                    data
                ) else null
            )
            is Success -> Success(
                data = conversion(
                    data
                )
            )
            is Error -> Error(
                data = if (data != null) conversion(data) else null,
                exception = exception
            )
        }
    }

    fun getNullableData(): T? {
        return when (this) {
            Initial -> null
            is Loading -> data
            is Success -> data
            is Error -> data
        }
    }
}

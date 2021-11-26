package com.thundermaps.apilib.android.impl.resources

import android.util.Log
import com.google.gson.Gson
import com.thundermaps.apilib.android.api.requests.SaferMeApiStatus
import com.thundermaps.apilib.android.api.responses.models.ResponseError
import com.thundermaps.apilib.android.api.responses.models.ResponseException
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import io.ktor.client.call.HttpClientCall
import io.ktor.util.toByteArray

suspend fun <T : Any> ResultHandler.processResult(call: HttpClientCall, gson: Gson, clazz: Class<T>): Result<T> {
    val status = SaferMeApiStatus.statusForCode(call.response.status.value)
    val responseString = String(call.response.content.toByteArray())
    return when (status) {
        SaferMeApiStatus.OK, SaferMeApiStatus.OTHER_200 -> {
            try {
                val response = gson.fromJson(
                    responseString,
                    clazz
                )
                Log.e("processResult", "result: $response")
                handleSuccess(response)
            } catch (exception: Exception) {
                handleException(exception)
            }
        }
        SaferMeApiStatus.NO_CONTENT -> handleSuccess(Unit as T)
        else -> {
            val exception = try {
                val responseError = gson.fromJson(
                    responseString,
                    ResponseError::class.java
                )
                ResponseException(responseError)
            } catch (exception: Exception) {
                exception
            }
            handleException(exception)
        }
    }
}

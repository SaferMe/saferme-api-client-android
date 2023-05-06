package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport
import com.thundermaps.apilib.android.api.fromJsonString
import com.thundermaps.apilib.android.api.requests.SaferMeApiStatus
import com.thundermaps.apilib.android.api.responses.models.ResponseError
import com.thundermaps.apilib.android.api.responses.models.ResponseException
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import io.ktor.client.call.HttpClientCall
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.toByteArray
import java.net.UnknownHostException

@KtorExperimentalAPI
@ExcludeFromJacocoGeneratedReport
suspend inline fun <reified T : Any> ResultHandler.processResult(
    call: HttpClientCall?,
    gson: Gson
): Result<T> {
    if (call == null) {
        return handleException(UnknownHostException())
    }
    val status = SaferMeApiStatus.statusForCode(call.response.status.value)
    val responseString = String(call.response.content.toByteArray())
    return when (status) {
        SaferMeApiStatus.OK, SaferMeApiStatus.OTHER_200, SaferMeApiStatus.ACCEPTED, SaferMeApiStatus.CREATED -> {
            try {
                val response = gson.fromJsonString<T>(responseString)

                handleSuccess(response)
            } catch (exception: Exception) {
                handleException(exception)
            }
        }
        SaferMeApiStatus.NO_CONTENT -> handleSuccess(Unit as T)
        else -> {
            val exception = try {
                val responseError: ResponseError = gson.fromJsonString(responseString)
                ResponseException(responseError)
            } catch (exception: Exception) {
                exception
            }
            handleException(exception)
        }
    }
}

@KtorExperimentalAPI
@ExcludeFromJacocoGeneratedReport
suspend inline fun <reified T : Any> ResultHandler.processResult(
    call: HttpClientCall,
    moshi: Moshi,
    gson: Gson
): Result<T> {
    val status = SaferMeApiStatus.statusForCode(call.response.status.value)
    val responseString = String(call.response.content.toByteArray())
    val adapter = moshi.adapter(T::class.java)
    return when (status) {
        SaferMeApiStatus.OK, SaferMeApiStatus.OTHER_200 -> {
            try {
                adapter.fromJson(responseString)?.let {
                    handleSuccess(it)
                } ?: handleException(
                    Exception("Unwanted data")
                )
            } catch (exception: Exception) {
                handleException(exception)
            }
        }
        SaferMeApiStatus.NO_CONTENT -> handleSuccess(Unit as T)
        else -> {
            val exception = try {
                val responseError: ResponseError = gson.fromJsonString(responseString)
                ResponseException(responseError)
            } catch (exception: Exception) {
                exception
            }
            handleException(exception)
        }
    }
}

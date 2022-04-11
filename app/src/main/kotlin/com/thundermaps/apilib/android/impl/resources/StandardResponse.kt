package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.thundermaps.apilib.android.api.fromJsonString
import com.thundermaps.apilib.android.api.requests.SaferMeApiStatus
import com.thundermaps.apilib.android.api.responses.models.ResponseError
import com.thundermaps.apilib.android.api.responses.models.ResponseException
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import io.ktor.client.call.HttpClientCall
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.toByteArray
import timber.log.Timber

@KtorExperimentalAPI
suspend inline fun <reified T : Any> ResultHandler.processResult(
    call: HttpClientCall,
    gson: Gson
): Result<T> {
    val status = SaferMeApiStatus.statusForCode(call.response.status.value)
    val responseString = String(call.response.content.toByteArray())
    Timber.e("responseString: $responseString")
    return when (status) {
        SaferMeApiStatus.OK, SaferMeApiStatus.OTHER_200 -> {
            try {
                val response = gson.fromJsonString<T>(responseString)
                Timber.e("response: $response")
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

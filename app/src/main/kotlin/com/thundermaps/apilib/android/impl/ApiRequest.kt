package com.thundermaps.apilib.android.impl

import com.thundermaps.apilib.android.api.responses.models.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException

// Define a custom exception for API errors
class ApiException(message: String) : Exception(message)

// Suspend function to handle the API request and deserialization
suspend inline fun <reified T : Any> HttpClient.apiRequest(
    requestBuilder: HttpRequestBuilder
): Result<T> = withContext(Dispatchers.IO) {
    try {
        val response = request<HttpResponse>(requestBuilder)

        if (response.status != HttpStatusCode.OK) {
            throw ClientRequestException(response)
        }

        val result = response.receive<T>()
        Result.Success(result)
    } catch (e: ClientRequestException) {
        Result.Error(exception = ApiException(e.message ?: "API request failed ${e.response.status}"))
    } catch (e: SerializationException) {
        Result.Error(exception = ApiException("JSON deserialization failed: ${e.message}"))
    } catch (e: Throwable) {
        Result.Error(exception = ApiException("An error occurred: ${e.message}"))
    }
}

package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiStatus
import com.thundermaps.apilib.android.api.resources.TeamResource
import com.thundermaps.apilib.android.api.responses.models.ResponseError
import com.thundermaps.apilib.android.api.responses.models.ResponseException
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.api.responses.models.Team
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.call
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.util.toByteArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamResourceImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : TeamResource {
    override suspend fun getTeams(parameters: RequestParameters): Result<List<Team>> {
        val call = getTeamsCall(parameters)
        val status = SaferMeApiStatus.statusForCode(call.response.status.value)
        val responseString = String(call.response.content.toByteArray())
        val typeToken = object : TypeToken<List<Team>>() {}.type
        return when (status) {
            SaferMeApiStatus.OK, SaferMeApiStatus.OTHER_200 -> {
                try {
                    val teams = gson.fromJson<List<Team>>(
                        responseString,
                        typeToken
                    )
                    resultHandler.handleSuccess(teams)
                } catch (exception: Exception) {
                    resultHandler.handleException(exception)
                }
            }
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
                resultHandler.handleException(exception)
            }
        }
    }

    private suspend inline fun getTeamsCall(
        parameters: RequestParameters
    ): HttpClientCall {
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = HttpMethod.Get
            url(AndroidClient.baseUrlBuilder(parameters).apply {
                encodedPath = "${this.encodedPath}${"teams"}"
            }.build())
        })
        return call
    }
}

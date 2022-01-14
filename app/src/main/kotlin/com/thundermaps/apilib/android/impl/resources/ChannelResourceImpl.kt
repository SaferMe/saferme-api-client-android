package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.ChannelResource
import com.thundermaps.apilib.android.api.responses.models.Channel
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.call
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.util.KtorExperimentalAPI
import javax.inject.Inject
import javax.inject.Singleton

@KtorExperimentalAPI
@Singleton
class ChannelResourceImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : ChannelResource {
    override suspend fun getChannels(
        parameters: RequestParameters,
        teamId: Long,
        fields: String
    ): Result<List<Channel>> {
        val call = getChannelsCall(parameters, teamId, fields)

        return resultHandler.processResult(call, gson)
    }

    private suspend fun getChannelsCall(
        parameters: RequestParameters,
        teamId: Long,
        fields: String
    ): HttpClientCall {
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = HttpMethod.Get
            url(AndroidClient.baseUrlBuilder(parameters).apply {
                encodedPath = "${encodedPath}teams/$teamId/channels?fields=$fields"
            }.build())
        })
        return call
    }
}

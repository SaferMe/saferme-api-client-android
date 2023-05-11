package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.thundermaps.apilib.android.api.com.thundermaps.isInternetAvailable
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.FormResource
import com.thundermaps.apilib.android.api.responses.models.Form
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FormResourceImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : FormResource {
    override suspend fun getForm(
        parameters: RequestParameters,
        channelId: Int
    ): Result<Form> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }

        val call = getFormCall(parameters, channelId)

        return resultHandler.processResult(call, gson)
    }

    override suspend fun getForms(
        parameters: RequestParameters
    ): Result<List<Form>> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }

        val call = getFormsCall(parameters)
        return resultHandler.processResult(call, gson)
    }

    private suspend fun getFormCall(
        parameters: RequestParameters,
        channelId: Int
    ): HttpClientCall {
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.request<HttpResponse> (
            HttpRequestBuilder().takeFrom(requestBuilder).apply {
                method = HttpMethod.Get
                url(
                    AndroidClient.baseUrlBuilder(parameters).apply {
                        encodedPath = "${encodedPath}channels/$channelId/form"
                    }.build()
                )
            }
        ).call
        return call
    }

    private suspend fun getFormsCall(
        parameters: RequestParameters
    ): HttpClientCall {
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.request<HttpResponse> (
            HttpRequestBuilder().takeFrom(requestBuilder).apply {
                method = HttpMethod.Get
                url(
                    AndroidClient.baseUrlBuilder(parameters).apply {
                        val extensionParams = parameters.parameters?.toUriParameters()?.let { "&$it" } ?: ""
                        encodedPath = "${encodedPath}forms?$extensionParams"
                    }.build()
                )
            }
        ).call
        return call
    }
}

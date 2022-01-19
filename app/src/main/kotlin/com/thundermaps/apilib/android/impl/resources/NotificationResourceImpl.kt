package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.NotificationResource
import com.thundermaps.apilib.android.api.responses.models.Notification
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
class NotificationResourceImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : NotificationResource {
    override suspend fun getNotifications(parameters: RequestParameters): Result<List<Notification>> {
        val call = getNotificationsCall(parameters)
        return resultHandler.processResult(call, gson)
    }

    private suspend fun getNotificationsCall(
        parameters: RequestParameters
    ): HttpClientCall {
        val (client, requestBuilder) = androidClient.client(parameters)
        val params = parameters.parameters?.let { data ->
            data.keys.joinToString("&") {
                "$it=${data[it]}"
            }
        }
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = HttpMethod.Get
            url(AndroidClient.baseUrlBuilder(parameters).apply {
                val path = "${encodedPath}notifications"
                encodedPath = params?.let { "$path?$it" } ?: path
            }.build())
        })
        return call
    }
}

package com.thundermaps.apilib.android.impl.resources

import androidx.annotation.VisibleForTesting
import com.thundermaps.apilib.android.api.com.thundermaps.isInternetAvailable
import com.thundermaps.apilib.android.api.requests.Constants
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.models.MapboxFeature
import com.thundermaps.apilib.android.api.resources.ShapeResource
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.call.call
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.toByteArray
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@KtorExperimentalAPI
@Singleton
class ShapeResourceImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val resultHandler: ResultHandler
) : ShapeResource {
    override suspend fun getShape(mapboxFeature: MapboxFeature): Result<String> {
        if (!HOST.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val parameters = RequestParameters(
            customRequestHeaders = hashMapOf(HttpHeaders.Accept to Constants.APPLICATION_JSON),
            credentials = null,
            host = HOST,
            api_version = 1
        )
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = HttpMethod.Get
            url(AndroidClient.baseUrlBuilder(parameters).apply {
                encodedPath =
                    "datasets/v${parameters.api_version}/${mapboxFeature.mapboxUser}/${mapboxFeature.mapboxDatasetId}/features?access_token=${mapboxFeature.mapboxAccessToken}"
            }.build())
        })

        return if (call.response.status == HttpStatusCode.OK) {
            val responseString = String(call.response.content.toByteArray())
            resultHandler.handleSuccess(responseString)
        } else {
            resultHandler.handleException(Exception(ERROR_MESSAGE))
        }
    }

    companion object {
        private const val HOST = "api.mapbox.com"
        @VisibleForTesting
        const val ERROR_MESSAGE = "There is an error with get features"
    }
}

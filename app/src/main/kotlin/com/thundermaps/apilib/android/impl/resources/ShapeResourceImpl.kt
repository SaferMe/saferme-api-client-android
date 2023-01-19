package com.thundermaps.apilib.android.impl.resources

import androidx.annotation.VisibleForTesting
import com.thundermaps.apilib.android.api.com.thundermaps.isInternetAvailable
import com.thundermaps.apilib.android.api.requests.Constants
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.models.ShapeParameterRequest
import com.thundermaps.apilib.android.api.resources.ShapeResource
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.resources.ShapeResourceImpl.Companion.VALUE_BETWEEN_TAGS
import io.github.dellisd.spatialk.geojson.FeatureCollection
import io.ktor.client.call.HttpClientCall
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.util.toByteArray
import java.net.UnknownHostException
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShapeResourceImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val resultHandler: ResultHandler
) : ShapeResource {
    private val parameters
        get() = RequestParameters(
            customRequestHeaders = hashMapOf(HttpHeaders.Accept to Constants.APPLICATION_JSON),
            credentials = null,
            host = HOST,
            api_version = 1
        )
    private var nextLink: String? = null
    private var nextShapeParameter: ShapeParameterRequest? = null
    override suspend fun getShape(shapeParameter: ShapeParameterRequest): Result<FeatureCollection> {
        nextShapeParameter = shapeParameter
        if (!HOST.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val parameters = parameters
        val (client, requestBuilder) = androidClient.client(parameters)
        val call = client.request<HttpResponse> (
            HttpRequestBuilder().takeFrom(requestBuilder).apply {
                method = HttpMethod.Get
                url(
                    AndroidClient.baseUrlBuilder(parameters).apply {
                        encodedPath =
                            "datasets/v${parameters.api_version}/${shapeParameter.mapboxUser}/${shapeParameter.mapboxDatasetId}/features?access_token=${shapeParameter.mapboxAccessToken}"
                    }.build()
                )
            }
        ).call

        return processResponse(call)
    }

    override suspend fun getNextShape(): Result<FeatureCollection>? {
        return nextLink?.let { link ->
            val path = link.replace("https://$HOST/", "")
            nextShapeParameter?.let { shapeParameter ->
                if (!HOST.isInternetAvailable()) {
                    resultHandler.handleException<String>(UnknownHostException())
                }

                val parameters = parameters
                val (client, requestBuilder) = androidClient.client(parameters)
                val call = client.request<HttpResponse> (
                    HttpRequestBuilder().takeFrom(requestBuilder).apply {
                        method = HttpMethod.Get
                        url(
                            AndroidClient.baseUrlBuilder(parameters).apply {
                                encodedPath = "$path&access_token=${shapeParameter.mapboxAccessToken}"
                            }.build()
                        )
                    }
                ).call
                processResponse(call)
            }
        }
    }

    private suspend fun processResponse(call: HttpClientCall) =
        if (call.response.status == HttpStatusCode.OK) {
            val responseString = String(call.response.content.toByteArray())
            nextLink = call.response.headers[LINK]?.let { link ->
                link.parserLink()
            }
            try {
                val featureCollection = FeatureCollection.fromJson(responseString)
                if (featureCollection.features.isNullOrEmpty()) nextLink = null
                resultHandler.handleSuccess(featureCollection)
            } catch (exception: Exception) {
                nextLink = null
                resultHandler.handleException(exception)
            }
        } else {
            nextLink = null
            resultHandler.handleException(Exception(ERROR_MESSAGE))
        }

    companion object {
        private const val HOST = "api.mapbox.com"

        @VisibleForTesting
        const val ERROR_MESSAGE = "There is an error with get features"

        @VisibleForTesting
        const val VALUE_BETWEEN_TAGS = "(?<=<).*(?=>)"
        private const val LINK = "Link"
    }
}

@VisibleForTesting
fun String?.parserLink(): String? {
    if (this == null) return null
    val pattern = Pattern.compile(VALUE_BETWEEN_TAGS)
    val matcher = pattern.matcher(this)
    return if (matcher.find()) {
        matcher.group()
    } else {
        null
    }
}

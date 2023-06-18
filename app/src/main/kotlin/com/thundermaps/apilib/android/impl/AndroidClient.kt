package com.thundermaps.apilib.android.impl

import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.thundermaps.apilib.android.api.ApiClient
import com.thundermaps.apilib.android.api.SaferMeCredentials
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.buildRequestParameters
import com.thundermaps.apilib.android.api.responses.models.DataValue
import com.thundermaps.apilib.android.api.responses.models.DataValueDecode
import com.thundermaps.apilib.android.api.responses.models.FieldType
import com.thundermaps.apilib.android.api.responses.models.FieldTypeDecode
import com.thundermaps.apilib.android.api.responses.models.FormValue
import com.thundermaps.apilib.android.api.responses.models.FormValueDecode
import io.ktor.client.HttpClient
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import javax.inject.Inject
import io.ktor.client.request.url as requestUrl

class AndroidClient @Inject constructor(private val apiClient: ApiClient) {
    fun client(params: RequestParameters): Pair<HttpClient, HttpRequestBuilder> = build(params)

    @Deprecated("Use the request(parameters: Map<String, String>?, path: String = \"\", method: HttpMethod = HttpMethod.Get) version of this method instead")
    fun build(params: RequestParameters, path: String = "", method: HttpMethod = HttpMethod.Get): Pair<HttpClient, HttpRequestBuilder> {
        val requestBuilder = getRequestBuilder(params, path, method)
        return Pair(apiClient.ktorClient, requestBuilder)
    }

    fun buildRequest(parameters: Map<String, String>?, path: String = "", method: HttpMethod = HttpMethod.Get): Pair<HttpClient, HttpRequestBuilder> {
        val requestBuilder = getRequestBuilder(parameters, path, method)
        return Pair(apiClient.ktorClient, requestBuilder)
    }

    // Widely used static builders/configuration (Assists with DRY code)
    companion object {
        private var requestBuilderBasic: HttpRequestBuilder = HttpRequestBuilder()

        // Store the most recently used credentials
        private var currentCredentials: SaferMeCredentials? = null
        // Default GSON Configuration
        @VisibleForTesting
        val gsonBuilder = GsonBuilder().apply {
            serializeNulls()
            disableHtmlEscaping()
            excludeFieldsWithoutExposeAnnotation()
            setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            registerTypeAdapter(FieldType::class.java, FieldTypeDecode())
            registerTypeAdapter(FormValue::class.java, FormValueDecode())
            registerTypeAdapter(DataValue::class.java, DataValueDecode())
        }

        // Reusable serializer configured with default options
        val gsonSerializer: Gson = gsonBuilder.create()

        val jsonSerializer = GsonSerializer {
            serializeNulls()
            disableHtmlEscaping()
            // Disable field exclusion to be consistent with the default GSON configuration
            // excludeFieldsWithoutExposeAnnotation()
            setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            registerTypeAdapter(FieldType::class.java, FieldTypeDecode())
            registerTypeAdapter(FormValue::class.java, FormValueDecode())
            registerTypeAdapter(DataValue::class.java, DataValueDecode())
        }

        // Reusable URL Builder
        fun baseUrlBuilder(params: RequestParameters): URLBuilder {
            return URLBuilder().apply {
                protocol = URLProtocol.HTTPS
                host = params.host
                port = params.port ?: 443
                encodedPath = "/api/v${params.api_version}/"
            }
        }

        fun getRequestBuilder(params: RequestParameters, path: String, method: HttpMethod): HttpRequestBuilder {
            // Reinitialize if users credentials have changed
            if (params.credentials != null && currentCredentials != params.credentials) {
                requestBuilderBasic = HttpRequestBuilder().apply {
                    val creds = params.credentials
                    currentCredentials = creds
                    if (creds != null) {
                        headers.append(HeaderType.xInstallationId, creds.InstallationId)
                        headers.append(HttpHeaders.Authorization, "Token token=${creds.ApiKey}")
                        headers.append(HeaderType.xAppId, creds.AppId)
                        if (creds.TeamId != null)
                            headers.append(HeaderType.xTeamId, creds.TeamId)
                    }
                    headers.append(HttpHeaders.Accept, "application/json, text/plain, */*")
                }
            }

            // Backward compatible with older versions implementation before ApiRequest.kt was introduced
            if (path.isNullOrEmpty()) {
                return if (params.customRequestHeaders.isEmpty()) {
                    requestBuilderBasic
                } else {
                    HttpRequestBuilder().takeFrom(requestBuilderBasic).apply {
                        params.customRequestHeaders.forEach { (key, value) ->
                            headers[key] = value
                        }
                    }
                }
            }

            return HttpRequestBuilder().takeFrom(requestBuilderBasic).apply {
                this.method = method
                params.customRequestHeaders.forEach { (key, value) ->
                    headers[key] = value
                }
                requestUrl(
                    baseUrlBuilder(params).apply {
                        encodedPath += path
                        params.parameters?.forEach { (key, value) ->
                            parameters.append(key, value)
                        }
                    }.build()
                )
            }
        }

        fun getRequestBuilder(parameters: Map<String, String>?, path: String, method: HttpMethod): HttpRequestBuilder {
            val requestParameters = buildRequestParameters(parameters)
            return getRequestBuilder(requestParameters, path, method)
        }
    }
}

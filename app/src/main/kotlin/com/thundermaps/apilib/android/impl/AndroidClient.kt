package com.thundermaps.apilib.android.impl

import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.thundermaps.apilib.android.api.ApiClient
import com.thundermaps.apilib.android.api.SaferMeCredentials
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.responses.models.DataValue
import com.thundermaps.apilib.android.api.responses.models.DataValueDecode
import com.thundermaps.apilib.android.api.responses.models.FieldType
import com.thundermaps.apilib.android.api.responses.models.FieldTypeDecode
import com.thundermaps.apilib.android.api.responses.models.FormValue
import com.thundermaps.apilib.android.api.responses.models.FormValueDecode
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import javax.inject.Inject

class AndroidClient @Inject constructor(private val apiClient: ApiClient) {
    private var requestBuilderTemplate = HttpRequestBuilder()

    // Store the most recently used credentials
    private var currentCredentials: SaferMeCredentials? = null

    @io.ktor.util.KtorExperimentalAPI
    fun client(params: RequestParameters): Pair<HttpClient, HttpRequestBuilder> {
        // Reinitialize if users credentials have changed
        if (currentCredentials != params.credentials) {
            requestBuilderTemplate = HttpRequestBuilder().apply {
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

        val requestBuilder = if (params.customRequestHeaders.isEmpty()) {
            requestBuilderTemplate
        } else {
            HttpRequestBuilder().takeFrom(requestBuilderTemplate).apply {
                params.customRequestHeaders.forEach { (key, value) ->
                    headers[key] = value
                }
            }
        }
        return Pair(apiClient.ktorClient, requestBuilder)
    }

    // Widely used static builders/configuration (Assists with DRY code)
    companion object {

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

        // Reusable URL Builder
        fun baseUrlBuilder(params: RequestParameters): URLBuilder {
            return URLBuilder().apply {
                protocol = URLProtocol.HTTPS
                host = params.host
                port = params.port ?: 443
                encodedPath = "/api/v${params.api_version}/"
            }
        }
    }
}

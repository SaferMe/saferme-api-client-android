package com.thundermaps.apilib.android.impl.resources

import apiRequest
import com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager
import com.thundermaps.apilib.android.api.requests.Constants.APPLICATION_JSON
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.BrandResource
import com.thundermaps.apilib.android.api.responses.models.Brand
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.HeaderType
import io.ktor.http.HttpHeaders
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BrandResourceImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val environmentManager: EnvironmentManager
) : BrandResource {
    fun createParameters(applicationId: String, host: String = "") =
        RequestParameters(
            customRequestHeaders = hashMapOf(
                HeaderType.xAppId to applicationId,
                HttpHeaders.Accept to APPLICATION_JSON
            ),
            credentials = null,
            host = host,
            api_version = 4,
            parameters = mapOf("fields" to BRAND_FIELDS.joinToString(","))
        )

    override suspend fun getBrand(applicationId: String): Result<Brand> {
        val parameters = createParameters(applicationId, environmentManager.environment.servers.first())
        val (client, requestBuilder) = androidClient.build(parameters, PATH)
        return client.apiRequest(requestBuilder)
    }

    companion object {
        const val PATH = "branded_app"
        val BRAND_FIELDS = listOf(
            "name",
            "bundle_id",
            "android_url",
            "oauth_providers",
            "layer_tilejson",
            "custom_user_fields",
            "default_location",
            "default_zoom",
            "safety_app"
        )
    }
}

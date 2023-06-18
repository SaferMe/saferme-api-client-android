package com.thundermaps.apilib.android.impl.resources

import com.thundermaps.apilib.android.api.resources.BrandResource
import com.thundermaps.apilib.android.api.responses.models.Brand
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.apiRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BrandResourceImpl @Inject constructor(
    private val androidClient: AndroidClient
) : BrandResource {
    override suspend fun getBrand(applicationId: String): Result<Brand> {
        val (client, requestBuilder) = androidClient.buildRequest(mapOf("fields" to BRAND_FIELDS.joinToString(",")), PATH)
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

package com.thundermaps.apilib.android.impl.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.CategoryResource
import com.thundermaps.apilib.android.api.responses.models.Category
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.apiRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryResourceImpl @Inject constructor(
    private val androidClient: AndroidClient
) : CategoryResource {
    override suspend fun getCategory(
        parameters: RequestParameters,
        channelId: Int
    ): Result<List<Category>> {
        val params = parameters.copy(parameters = mapOf("fields" to FIELDS.joinToString(",")) + (parameters.parameters ?: emptyMap()))
        val (client, requestBuilder) = androidClient.build(params, "channels/$channelId/categories")

        return client.apiRequest(requestBuilder)
    }

    companion object {
        internal val FIELDS = listOf(
            "id", "name", "depth", "parent", "label_name", "pin_color", "position", "pin_appearance"
        )
    }
}

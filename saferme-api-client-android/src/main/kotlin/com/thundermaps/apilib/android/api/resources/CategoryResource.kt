package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.responses.models.Category
import com.thundermaps.apilib.android.api.responses.models.Result

interface CategoryResource {
    suspend fun getCategory(
        parameters: RequestParameters,
        channelId: Int,
        fields: String = CATEGORY_FIELDS.joinToString(",")
    ): Result<List<Category>>

    companion object {
        internal val CATEGORY_FIELDS = listOf(
            "id", "name", "depth", "parent", "label_name", "pin_color", "position", "pin_appearance"
        )
    }
}

package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.responses.models.Category
import com.thundermaps.apilib.android.api.responses.models.Result

interface CategoryResource {
    suspend fun getCategory(
        parameters: RequestParameters,
        channelId: Int
    ): Result<List<Category>>
}

package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.models.MapboxFeature
import com.thundermaps.apilib.android.api.responses.models.Result

interface ShapeResource {
    suspend fun getShape(mapboxFeature: MapboxFeature): Result<String>
}

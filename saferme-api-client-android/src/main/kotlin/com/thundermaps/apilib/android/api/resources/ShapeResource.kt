package com.thundermaps.apilib.android.api.resources

import com.mapbox.geojson.FeatureCollection
import com.thundermaps.apilib.android.api.requests.models.ShapeParameterRequest
import com.thundermaps.apilib.android.api.responses.models.Result

interface ShapeResource {
    suspend fun getShape(shapeParameter: ShapeParameterRequest): Result<FeatureCollection>
    suspend fun getNextShape(): Result<FeatureCollection>?
}

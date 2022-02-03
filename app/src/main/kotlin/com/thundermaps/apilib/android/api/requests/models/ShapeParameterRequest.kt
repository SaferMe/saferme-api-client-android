package com.thundermaps.apilib.android.api.requests.models

data class ShapeParameterRequest(
    val mapboxUser: String,
    val mapboxDatasetId: String,
    val mapboxAccessToken: String
)

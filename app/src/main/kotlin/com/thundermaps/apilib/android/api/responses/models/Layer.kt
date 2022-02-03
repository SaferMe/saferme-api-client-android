package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport

@ExcludeFromJacocoGeneratedReport
data class Layer(
    val id: String,
    val type: String,
    val source: LayerSource,
    @SerializedName(value = MIN_ZOOM) val minZoom: Float,
    @SerializedName(value = MAX_ZOOM) val maxZoom: Float,
    @SerializedName(value = BASED_LAYER_ID) val basedLayerId: String,
    @SerializedName(value = STYLE_NAME) val styleName: String
) {
    companion object {
        private const val MIN_ZOOM = "minzoom"
        private const val MAX_ZOOM = "maxzoom"
        private const val BASED_LAYER_ID = "based_layer_id"
        private const val STYLE_NAME = "style_name"
    }
}

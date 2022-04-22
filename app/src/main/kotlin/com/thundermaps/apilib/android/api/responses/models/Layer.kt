package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport

@ExcludeFromJacocoGeneratedReport
data class Layer(
    @Expose val id: String,
    @Expose val type: String,
    @Expose val source: LayerSource,
    @SerializedName(value = MIN_ZOOM) @Expose val minZoom: Float,
    @SerializedName(value = MAX_ZOOM) @Expose val maxZoom: Float,
    @SerializedName(value = BASED_LAYER_ID) @Expose val basedLayerId: String,
    @SerializedName(value = STYLE_NAME) @Expose val styleName: String
) {
    companion object {
        private const val MIN_ZOOM = "minzoom"
        private const val MAX_ZOOM = "maxzoom"
        private const val BASED_LAYER_ID = "based_layer_id"
        private const val STYLE_NAME = "style_name"
    }
}

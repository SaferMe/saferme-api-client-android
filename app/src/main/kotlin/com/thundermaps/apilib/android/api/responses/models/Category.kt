package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Category(
    @Expose val id: Int,
    @Expose val name: String,
    @SerializedName(value = LABEL_NAME) @Expose val labelName: String?,
    @Expose val depth: Int,
    @SerializedName(value = PIN_COLOR) @Expose val pinColor: String,
    @Expose val position: Int,
    @SerializedName(value = PARENT_ID) @Expose val parentId: Int? = null,
    @SerializedName(value = PIN_APPEARANCE) @Expose val pinAppearance: String
) {
    companion object {
        const val LABEL_NAME = "label_name"
        const val PIN_COLOR = "pin_color"
        const val PARENT_ID = "parent_id"
        const val PIN_APPEARANCE = "pin_appearance"
    }
}

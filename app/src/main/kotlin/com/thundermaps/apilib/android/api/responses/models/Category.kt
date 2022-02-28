package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Int,
    val name: String,
    @SerializedName(value = LABEL_NAME) val labelName: String?,
    val depth: Int,
    @SerializedName(value = PIN_COLOR) val pinColor: String,
    val position: Int,
    @SerializedName(value = PARENT_ID) val parentId: Int? = null
) {
    companion object {
        const val LABEL_NAME = "label_name"
        const val PIN_COLOR = "pin_color"
        const val PARENT_ID = "parent_id"
    }
}

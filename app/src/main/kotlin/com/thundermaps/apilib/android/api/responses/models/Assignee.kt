package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Assignee(
    @Expose val id: Long,
    @SerializedName("first_name") @Expose val firstName: String,
    @SerializedName("last_name") @Expose val lastName: String
)

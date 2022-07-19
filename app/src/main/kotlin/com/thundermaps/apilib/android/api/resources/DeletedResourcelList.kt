package com.thundermaps.apilib.android.api.resources

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DeletedResourcelList(
    @SerializedName("deleted_resources") @Expose val deletedResource: List<ChannelId>
)

data class ChannelId(
    @Expose val uuid: String
)

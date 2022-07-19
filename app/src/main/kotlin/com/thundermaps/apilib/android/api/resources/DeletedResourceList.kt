package com.thundermaps.apilib.android.api.resources

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DeletedResourceList(
    @SerializedName("deleted_resources") @Expose val deletedResource: List<ResourceId>
)

data class ResourceId(
    @Expose val uuid: String
)

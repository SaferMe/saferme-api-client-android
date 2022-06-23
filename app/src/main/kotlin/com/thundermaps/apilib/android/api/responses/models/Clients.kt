package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.impl.AndroidClient.Companion.gsonSerializer

data class Clients(
    @SerializedName("push_notifications_enabled") @Expose val pushNotificationEnabled: Boolean,
    @Expose val radius: Float,
    @SerializedName("is_push_capable") @Expose val isPushCapable: Boolean,
    @Expose val badge: Int,
    @Expose val channels: List<Int>
) {
    companion object {
        fun toJsonString(): String = gsonSerializer.toJson(this)
        fun of(json: String): Clients = gsonSerializer.fromJson(json, Clients::class.java)
    }
}

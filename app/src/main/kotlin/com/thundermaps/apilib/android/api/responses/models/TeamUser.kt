package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TeamUser(
    @Expose val id: Long,
    @SerializedName("user_id") @Expose val userId: Long,
    @SerializedName("agreed_to_join") @Expose val agreedToJoin: Boolean,
    @SerializedName("bluetooth_enabled") @Expose val bluetoothEnabled: Boolean,
    @SerializedName("location_enabled") @Expose val locationEnabled: Boolean,
    @SerializedName("push_notification_enabled") @Expose val pushNotificationEnabled: Boolean,
    @SerializedName("first_name") @Expose val firstName: String,
    @SerializedName("last_name") @Expose val lastName: String,
    @Expose val email: String,
    @Expose val role: String?
)

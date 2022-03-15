package com.thundermaps.apilib.android.api.requests.models

import com.google.gson.annotations.SerializedName

data class UpdatePasswordBody(
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("password") val newPassword: String
)

data class UpdateNameBody(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String
)

data class EmailBody(val email: String)

data class UpdateContactNumberBody(@SerializedName("contact_number") val contactNumber: String)

data class UpdateAddressBody(val address: String)

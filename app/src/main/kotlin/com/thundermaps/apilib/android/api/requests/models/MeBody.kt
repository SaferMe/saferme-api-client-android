package com.thundermaps.apilib.android.api.requests.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdatePasswordBody(
    @SerializedName("current_password") @Expose val currentPassword: String,
    @SerializedName("password") @Expose val newPassword: String
)

data class UpdateNameBody(
    @SerializedName("first_name") @Expose val firstName: String,
    @SerializedName("last_name") @Expose val lastName: String
)

data class EmailBody(@Expose val email: String)

data class UpdateContactNumberBody(@SerializedName("contact_number") @Expose val contactNumber: String)

data class UpdateAddressBody(@Expose val address: String)

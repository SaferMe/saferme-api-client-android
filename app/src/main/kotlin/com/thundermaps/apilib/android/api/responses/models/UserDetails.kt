package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserDetails(
    @SerializedName("accepted_terms_version") @Expose val acceptedTermsVersion: Int,
    @Expose val address: String?,
    @SerializedName("contact_number") @Expose val contactNumber: String?,
    @Expose val email: String,
    @Expose val id: Long,
    @SerializedName("first_name") @Expose val firstName: String,
    @SerializedName("last_name") @Expose val lastName: String,
    @SerializedName("email_notifications_enabled") @Expose val emailNotificationEnabled: Boolean,
    @SerializedName("personal_account_option") @Expose val personalAccountOption: Boolean
)

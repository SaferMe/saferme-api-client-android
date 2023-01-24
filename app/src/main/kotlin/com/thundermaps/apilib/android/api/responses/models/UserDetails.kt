package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserDetails(
    @Expose val id: Long,
    @SerializedName("accepted_terms_version") @Expose val acceptedTermsVersion: Int,
    @Expose val avatar: Avatar,
    @SerializedName("contact_number") @Expose val contactNumber: String?,
    @Expose val email: String,
    @SerializedName("email_notifications_enabled") @Expose val emailNotificationEnabled: Boolean?,
    @Expose val address: String?,
    @SerializedName("first_name") @Expose val firstName: String,
    @SerializedName("last_name") @Expose val lastName: String,
    @SerializedName("gdpr_accept") @Expose val gdprAccept: Boolean,
    @SerializedName("gdpr_version") @Expose val gdprVersion: Int,
    @SerializedName("personal_account_option") @Expose val personalAccountOption: Boolean?
)

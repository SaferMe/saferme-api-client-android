package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Sessions(
    @SerializedName("auth_token") @Expose val apiKey: String,
    @SerializedName("consent_required") @Expose val consentRequired: Boolean,
    @SerializedName("team_id") @Expose val teamId: Long? = null,
    @SerializedName("user_id") @Expose val userId: Long,
    @SerializedName("personal_account_option") @Expose val personalAccountOption: Boolean,
    @SerializedName("profile_details_pending") @Expose val profileDetailsPending: Boolean,
    @SerializedName("password_update_pending") @Expose val passwordUpdatePending: Boolean
)

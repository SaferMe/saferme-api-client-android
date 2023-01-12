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
    @SerializedName("password_update_pending") @Expose val passwordUpdatePending: Boolean,
    @SerializedName("client_uuid") @Expose val clientUuid: String? = null,
    @SerializedName("installation_id") @Expose val installationId: String? = null
)

data class Profile(
    @SerializedName("user_id") @Expose val userId: Long,
    @SerializedName("preferred_team_id") @Expose val preferredTeamId: Long? = null,
    @SerializedName("user_uuid") @Expose val userUuid: String,
    @SerializedName("personal_account_option") @Expose val personalAccountOption: Boolean,
    @SerializedName("consent_required") @Expose val consentRequired: Boolean,
    @SerializedName("profile_details_pending") @Expose val profileDetailsPending: Boolean,
    @SerializedName("password_update_pending") @Expose val passwordUpdatePending: Boolean
)

data class SessionV4(
    @SerializedName("access_token") @Expose val accessToken: String,
    @SerializedName("refresh_token") @Expose val refreshToken: String,
    @SerializedName("token_expire_at") @Expose val tokenExpireAt: String,
    @SerializedName("app_bundle_id") @Expose val appBundleId: String,
    @SerializedName("branded_app_id") @Expose val brandedAppId: Long,
    @SerializedName("client_uuid") @Expose val clientUuid: String? = null,
    @SerializedName("profile") @Expose val profile: Profile
)

data class Session(
    @SerializedName("session") @Expose val session: SessionV4
) {
    fun toSessions() = Sessions(
        apiKey = session.accessToken,
        consentRequired = session.profile.consentRequired,
        teamId = session.profile.preferredTeamId,
        userId = session.profile.userId,
        personalAccountOption = session.profile.personalAccountOption,
        profileDetailsPending = session.profile.profileDetailsPending,
        passwordUpdatePending = session.profile.passwordUpdatePending,
        clientUuid = session.clientUuid,
        installationId = null
    )
}

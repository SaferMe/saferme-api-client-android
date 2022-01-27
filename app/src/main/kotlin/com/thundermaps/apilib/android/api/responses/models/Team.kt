package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thundermaps.domain.models.DiffItem

data class Team(
    @SerializedName(CONTACT_TRACING_ENABLED) @Expose val contactTracingEnabled: Boolean,
    @SerializedName(FEATURE_TASKS_ENABLED) @Expose val featureTasksEnabled: Boolean,
    @SerializedName(FORM_CONTACT_TRACING_ENABLED) @Expose val formContactTracingEnabled: Boolean,
    @SerializedName(GUESTS_ENABLED) @Expose val guestsEnabled: Boolean,
    @Expose val id: Long,
    @Expose val industry: String?,
    @Expose val location: String?,
    @Expose val name: String,
    @SerializedName(RISK_REGISTER_ENABLED) @Expose val riskRegisterEnabled: Boolean,
    @SerializedName(SSO_REQUIRED) @Expose val ssoRequired: Boolean,
    @SerializedName(SSO_TEAM_ID) @Expose val ssoTeamId: String,
    @SerializedName(USER_TIMEOUT) @Expose val userTimeout: String?,
    @SerializedName(IS_ADMIN_OF) @Expose val isAdmin: Boolean?,
    @SerializedName(IS_MANAGER_OF) @Expose val isManager: Boolean?,
    @SerializedName(IS_OWNER_OF) @Expose val isOwner: Boolean?,
    @SerializedName(WEARABLES_ENABLED) @Expose val wearablesEnabled: Boolean?,
    @SerializedName(MAPBOX_USERNAME) @Expose val mapboxUserName: String?,
    @SerializedName(MAPBOX_DATASET_ID) @Expose val mapboxDataSetId: String?,
    @SerializedName(MAPBOX_ACCESS_TOKEN) @Expose val mapboxAccessToken: String?

) : DiffItem {
    override val uniqueId: Any
        get() = id

    companion object {
        const val CONTACT_TRACING_ENABLED = "contact_tracing_enabled"
        const val FEATURE_TASKS_ENABLED = "feature_tasks_enabled"
        const val FORM_CONTACT_TRACING_ENABLED = "form_contact_tracing_enabled"
        const val GUESTS_ENABLED = "guests_enabled"
        const val RISK_REGISTER_ENABLED = "risk_register_enabled"
        const val SSO_REQUIRED = "sso_required"
        const val SSO_TEAM_ID = "sso_team_id"
        const val USER_TIMEOUT = "user_timeout"
        const val IS_MANAGER_OF = "is_manager_of"
        const val IS_ADMIN_OF = "is_admin_of"
        const val IS_OWNER_OF = "is_owner_of"
        const val WEARABLES_ENABLED = "wearables_enabled"
        const val MAPBOX_USERNAME = "mapbox_username"
        const val MAPBOX_DATASET_ID = "mapbox_dataset_id"
        const val MAPBOX_ACCESS_TOKEN = "mapbox_access_token"
    }
}

package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thundermaps.domain.models.DiffItem

data class Team(
    @SerializedName("contact_tracing_enabled") @Expose val contactTracingEnabled: Boolean,
    @SerializedName("feature_tasks_enabled") @Expose val featureTasksEnabled: Boolean,
    @SerializedName("form_contact_tracing_enabled") @Expose val formContactTracingEnabled: Boolean,
    @SerializedName("guests_enabled") @Expose val guestsEnabled: Boolean,
    @Expose val id: Long,
    @Expose val industry: String?,
    @Expose val location: String?,
    @Expose val name: String,
    @SerializedName("risk_register_enabled") @Expose val riskRegisterEnabled: Boolean,
    @SerializedName("sso_required") @Expose val ssoRequired: Boolean,
    @SerializedName("sso_team_id") @Expose val ssoTeamId: String,
    @SerializedName("user_timeout") @Expose val userTimeout: String?,
    @SerializedName("is_admin_of") @Expose val isAdmin: Boolean?,
    @SerializedName("is_manager_of") @Expose val isManager: Boolean?,
    @SerializedName("is_owner_of") @Expose val isOwner: Boolean?,
    @SerializedName("wearables_enabled") @Expose val wearablesEnabled: Boolean?
) : DiffItem {
    override val uniqueId: Any
        get() = id
}

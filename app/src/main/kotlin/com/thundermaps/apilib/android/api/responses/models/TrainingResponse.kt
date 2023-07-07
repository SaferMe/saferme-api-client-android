package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.SerializedName

data class TrainingResponse(
    @SerializedName(ID) val id: Int? = null,
    @SerializedName(ATTACHMENT_FILENAME) val attachmentFilename: String? = null,
    @SerializedName(ATTACHMENT_URL) val attachmentUrl: String? = null,
    @SerializedName(CATEGORY) val category: String? = null,
    @SerializedName(COMPETENCY_LEVEL_ID) val competencyLevelId: String? = null,
    @SerializedName(COMPETENCY_LEVEL_LABEL) val competencyLevelLabel: String? = null,
    @SerializedName(COMPETENCY_LEVEL_VALUE) val competencyLevelValue: String? = null,
    @SerializedName(COMPLETED_TEAM_USER_FULL_NAME) val completedTeamUserFullName: String? = null,
    @SerializedName(COMPLETED_TEAM_USER_ID) val completedTeamUserId: Int? = null,
    @SerializedName(EXPIRES_AT) val expiresAt: String? = null,
    @SerializedName(ISSUED_AT) val issuedAt: String? = null,
    @SerializedName(MODULE_NAME) val moduleName: String? = null,
    @SerializedName(SIGN_OFF_TEAM_USER_FULL_NAME) val signOffTeamUserFullName: String? = null,
    @SerializedName(SIGN_OFF_TEAM_USER_ID) val signOffTeamUserId: String? = null,
    @SerializedName(TRAINING_MODULE_ID) val trainingModuleId: Int? = null
) {
    companion object {
        const val ID = "id"
        const val ATTACHMENT_FILENAME = "attachment_filename"
        const val ATTACHMENT_URL = "attachment_url"
        const val CATEGORY = "category"
        const val COMPETENCY_LEVEL_ID = "competency_level_id"
        const val COMPETENCY_LEVEL_LABEL = "competency_level_label"
        const val COMPETENCY_LEVEL_VALUE = "competency_level_value"
        const val COMPLETED_TEAM_USER_FULL_NAME = "completed_team_user_full_name"
        const val COMPLETED_TEAM_USER_ID = "completed_team_user_id"
        const val EXPIRES_AT = "expires_at"
        const val ISSUED_AT = "issued_at"
        const val MODULE_NAME = "module_name"
        const val SIGN_OFF_TEAM_USER_FULL_NAME = "sign_off_team_user_full_name"
        const val SIGN_OFF_TEAM_USER_ID = "sign_off_team_user_id"
        const val TRAINING_MODULE_ID = "training_module_id"
    }
}

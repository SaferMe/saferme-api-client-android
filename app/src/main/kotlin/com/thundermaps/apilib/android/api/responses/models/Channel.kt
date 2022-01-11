package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.SerializedName

data class Channel(
    @SerializedName(ALLOW_PUBLIC_COMMENTS) val allowPublicComments: Boolean,
    @SerializedName(ALLOW_PUBLIC_VIEWERS) val allowPublicViewers: Boolean,
    @SerializedName(ALLOW_USER_DELETE_OWN_REPORTS) val allowUserDeleteOwnReports: Boolean,
    @SerializedName(ARE_NEW_REPORTS_ANONYMOUS) val areNewReportsAnonymous: Boolean,
    @SerializedName(BANNERS_ENABLED) val isBannersEnabled: Boolean,
    @SerializedName(CATEGORY_ID) val categoryId: Long,
    val description: String,
    @SerializedName(FORM_LOCKED) val isFormLocked: Boolean?,
    @SerializedName(HAZARD_CHANNEL) val isHazardChannel: Boolean?,
    val id: Long,
    @SerializedName(IS_ADDON_CHANNEL) val isAddonChannel: Boolean,
    @SerializedName(IS_DELETABLE_BY) val isDeletableBy: Boolean?,
    @SerializedName(IS_MANAGEABLE_BY) val isManageableBy: Boolean,
    @SerializedName(IS_OPERABLE_BY) val isOperableBy: Boolean,
    @SerializedName(IS_REPORTABLE_BY) val isReportableBy: Boolean,
    @SerializedName(LAST_REPORT_DATE) val lastReportDate: String?,
    val logo: Avatar?,
    @SerializedName(MEMBER_COUNT) val memberCount: Int? = 0,
    val moderated: Boolean,
    val name: String,
    @SerializedName(PIN_URLS) val pinUrls: PinUrls?,
    @SerializedName(REPORTS_COUNT) val reportsCount: Int,
    @SerializedName(RISK_CONTROLS_EDITABILITY) val riskControlsEditablity: String,
    val slug: String,
    @SerializedName(STANDARD_CHANNEL) val standardChannel: Boolean,
    @SerializedName(TEAM_ID) val teamId: Long,
    @SerializedName(TUNE_IN_COUNT) val tuneInCount: Int? = 0
) {
    companion object {
        const val ALLOW_PUBLIC_COMMENTS = "allow_public_comments"
        const val ALLOW_PUBLIC_VIEWERS = "allow_public_viewers"
        const val ALLOW_USER_DELETE_OWN_REPORTS = "allow_user_delete_own_reports"
        const val ARE_NEW_REPORTS_ANONYMOUS = "are_new_reports_anonymous"
        const val BANNERS_ENABLED = "banners_enabled"
        const val CATEGORY_ID = "category_id"
        const val FORM_LOCKED = "form_locked"
        const val HAZARD_CHANNEL = "hazard_channel"
        const val IS_ADDON_CHANNEL = "is_addon_channel"
        const val IS_DELETABLE_BY = "is_deletable_by"
        const val IS_MANAGEABLE_BY = "is_manageable_by"
        const val IS_OPERABLE_BY = "is_operable_by"
        const val IS_REPORTABLE_BY = "is_reportable_by"
        const val LAST_REPORT_DATE = "last_report_date"
        const val MEMBER_COUNT = "member_count"
        const val PIN_URLS = "pin_urls"
        const val REPORTS_COUNT = "reports_count"
        const val RISK_CONTROLS_EDITABILITY = "risk_controls_editability"
        const val STANDARD_CHANNEL = "standard_channel"
        const val TEAM_ID = "team_id"
        const val TUNE_IN_COUNT = "tune_in_count"
    }
}

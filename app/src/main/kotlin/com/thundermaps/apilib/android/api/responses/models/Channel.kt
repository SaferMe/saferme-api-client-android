package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Channel(
    @SerializedName(ALLOW_PUBLIC_COMMENTS) @Expose val allowPublicComments: Boolean,
    @SerializedName(ALLOW_PUBLIC_VIEWERS) @Expose val allowPublicViewers: Boolean,
    @SerializedName(ALLOW_USER_DELETE_OWN_REPORTS) @Expose val allowUserDeleteOwnReports: Boolean,
    @SerializedName(ARE_NEW_REPORTS_ANONYMOUS) @Expose val areNewReportsAnonymous: Boolean,
    @SerializedName(BANNERS_ENABLED) @Expose val isBannersEnabled: Boolean,
    @SerializedName(CATEGORY_ID) @Expose val categoryId: Long,
    @Expose val description: String,
    @SerializedName(FORM_LOCKED) @Expose val isFormLocked: Boolean?,
    @SerializedName(HAZARD_CHANNEL) @Expose val isHazardChannel: Boolean?,
    @Expose val id: Long,
    @Expose val uuid: String,
    @SerializedName(IS_ADDON_CHANNEL) @Expose val isAddonChannel: Boolean,
    @SerializedName(IS_DELETABLE_BY) @Expose val isDeletableBy: Boolean?,
    @SerializedName(IS_MANAGEABLE_BY) @Expose val isManageableBy: Boolean,
    @SerializedName(IS_OPERABLE_BY) @Expose val isOperableBy: Boolean,
    @SerializedName(IS_REPORTABLE_BY) @Expose val isReportableBy: Boolean,
    @SerializedName(LAST_REPORT_DATE) @Expose val lastReportDate: String?,
    @SerializedName(MEMBER_COUNT) @Expose val memberCount: Int? = 0,
    @Expose val moderated: Boolean,
    @Expose val name: String,
    @SerializedName(PIN_URLS) @Expose val pinUrls: PinUrls?,
    @SerializedName(REPORTS_COUNT) @Expose val reportsCount: Int,
    @SerializedName(RISK_CONTROLS_EDITABILITY) @Expose val riskControlsEditablity: String,
    @Expose val slug: String,
    @SerializedName(STANDARD_CHANNEL) @Expose val standardChannel: Boolean,
    @SerializedName(TEAM_ID) @Expose val teamId: Long,
    @SerializedName(TUNE_IN_COUNT) @Expose val tuneInCount: Int? = 0
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

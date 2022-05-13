package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport
import com.thundermaps.apilib.android.api.resources.SaferMeDatum

@ExcludeFromJacocoGeneratedReport
data class ReportState(
    @Expose val assignable: Boolean = false,
    @Expose val id: Int = 0,
    @Expose val loudness: String = "",
    @Expose val name: String = "",
    @Expose @SerializedName("account_id") val channelId: Int? = null,
    @Expose @SerializedName("assign_to_supervisor") val isAssignedToSupervisor: Boolean? = null,
    @Expose @SerializedName("assignment_due_timeout") val assignmentDueTimeout: Long? = null,
    @Expose @SerializedName("auto_archive") val isAutoArchive: Boolean? = null,
    @Expose @SerializedName("default_assignee_id") val defaultAssigneeId: Int? = null,
    @Expose val editability: String? = null,
    @Expose val notify: String? = null,
    @Expose val position: Int? = null,
    @Expose val slug: String? = null,
    @Expose val timeout: Int? = null,
    @Expose val uuid: String? = null,
    @Expose val visibility: String? = null
): SaferMeDatum

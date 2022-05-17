package com.thundermaps.apilib.android.api.resources

import com.google.gson.annotations.Expose
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport

@ExcludeFromJacocoGeneratedReport
data class MarkAsIncomplete(

    @Expose
    val uuid: String? = null,

    @Expose
    val completed_by_id: String? = null,

    @Expose
    val completed_at: String? = null

) : SaferMeDatum {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("------------------------------------------------------------------------------\n")
        sb.append("TASK: $uuid\n")
        sb.append("------------------------------------------------------------------------------\n")
        sb.append("Status:\t\tCOMPLETED by $completed_by_id on $completed_at")
        return sb.toString()
    }
}

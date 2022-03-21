package com.thundermaps.apilib.android.api.resources

import com.google.gson.annotations.Expose
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@ExcludeFromJacocoGeneratedReport
data class Task(

    @Expose
    val report_uuid: String? = null,

    @Expose
    val uuid: String? = null,

    @Expose
    val creator_id: Int? = null,

    @Expose
    val client_created_at: Date? = null,

    @Expose
    val title: String? = null,

    @Expose
    val description: String? = null,

    @Expose
    val assignee_id: Int? = null,

    @Expose
    val completed_by_id: Int? = null,

    @Expose
    val completed_at: Date? = null

) : SaferMeDatum {
    override fun toString(): String {
        val sb = StringBuilder()
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US)
        sb.append("------------------------------------------------------------------------------\n")
        sb.append("TASK: $uuid\n")
        sb.append("------------------------------------------------------------------------------\n")
        sb.append("Title:\t\t$title\n")
        sb.append("Description:\t\t $description\n")
        sb.append("Report:\t\t$report_uuid\n")
        sb.append("Creator:\t\t$creator_id\n")
        sb.append("Created At:\t\t${fmt.format(client_created_at)}\n")
        if (assignee_id != null)
            sb.append("Assigned To:\t\t$assignee_id\n")
        if (completed_at == null) {
            sb.append("Status:\t\tINCOMPLETE")
        } else {
            sb.append("Status:\t\tCOMPLETED by $completed_by_id on ${fmt.format(completed_at)}\n")
        }
        return sb.toString()
    }
}

interface TaskResource : SaferMeResource<Task>,
    Creatable<Task>, Readable<Task>, Indexable<Task>, Updatable<Task>, Deletable<Task>

package com.thundermaps.apilib.android.api.resources

import com.google.gson.annotations.Expose
data class TracedContacts(
    @Expose
    val traced_contacts: List<TracedContact> = ArrayList()
) : SaferMeDatum {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("tracedContacts $traced_contacts")
        return sb.toString()
    }
}

data class TracedContact(
    @Expose
    val timestamp: String? = null,

    @Expose
    val trace_type: String = "bluetooth",

    @Expose
    val contact_client_uuid: String = "",

    @Expose
    val meta: String = "",

    @Expose
    val distance: Int = 0,

    @Expose
    val location: String? = null
)
interface TracedContactsResource : Creatable<TracedContacts>

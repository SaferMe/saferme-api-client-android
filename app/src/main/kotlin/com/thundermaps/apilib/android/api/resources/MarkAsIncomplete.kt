package com.thundermaps.apilib.android.api.resources

import com.google.gson.annotations.Expose
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport
import java.util.Date

@ExcludeFromJacocoGeneratedReport
data class MarkAsIncomplete(

    @Expose
    val completed_by_id: Int? = null,

    @Expose
    val completed_at: Date? = null

) : SaferMeDatum

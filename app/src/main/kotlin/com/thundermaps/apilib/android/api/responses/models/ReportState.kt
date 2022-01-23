package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport

@ExcludeFromJacocoGeneratedReport
data class ReportState(
    @Expose val assignable: Boolean = false,
    @Expose val id: Int = 0,
    @Expose val loudness: String = "",
    @Expose val name: String = ""
)

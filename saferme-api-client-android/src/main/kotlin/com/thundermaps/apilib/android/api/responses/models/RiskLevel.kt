package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose

data class RiskLevel(
    @Expose val score: Int = 0,
    @Expose val label: String = "",
    @Expose val color: String = ""
)

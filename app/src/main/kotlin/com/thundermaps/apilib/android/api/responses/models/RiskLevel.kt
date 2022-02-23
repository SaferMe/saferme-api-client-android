package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
interface RiskItem {
    val name: String
}

data class RiskLevel(
    @Expose val score: Int = 0,
    @Expose val label: String = "",
    @Expose val color: String = ""
) : RiskItem {
    override val name: String
        get() = label
}

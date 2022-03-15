package com.thundermaps.apilib.android.api.resources

import com.google.gson.annotations.Expose

data class Location(

    @Expose
    val latitude: Double = 0.0,

    @Expose
    val longitude: Double = 0.0
)

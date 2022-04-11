package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose

data class Avatar(
    @Expose val mini: String,
    @Expose val small: String,
    @Expose val medium: String,
    @Expose val large: String,
    @Expose val huge: String
)

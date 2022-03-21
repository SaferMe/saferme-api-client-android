package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose

data class SsoSessions(
    @Expose val status: String,
    @Expose val strategy: String,
    @Expose val session: Sessions
)

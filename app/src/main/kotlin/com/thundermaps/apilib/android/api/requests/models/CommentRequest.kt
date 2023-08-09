package com.thundermaps.apilib.android.api.requests.models

import com.google.gson.annotations.Expose

data class CommentRequest(
    @Expose val uuid: String? = null,
    @Expose val content: String? = null,
    @Expose val isAnonymous: Boolean? = null,
    @Expose val reportId: Int? = null
)

package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose

data class Form(
    @Expose val id: Int,
    @Expose val name: String,
    @Expose val version: Int,
    @Expose val fields: List<FormField>
)

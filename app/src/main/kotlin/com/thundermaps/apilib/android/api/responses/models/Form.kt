package com.thundermaps.apilib.android.api.responses.models

data class Form(
    val id: Int,
    val name: String,
    val version: Int,
    val fields: List<FormField>
)

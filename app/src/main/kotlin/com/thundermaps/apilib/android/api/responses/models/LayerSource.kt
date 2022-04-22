package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose

data class LayerSource(@Expose val type: String, @Expose val tiles: List<String>)

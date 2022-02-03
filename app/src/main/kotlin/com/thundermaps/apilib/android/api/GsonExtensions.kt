package com.thundermaps.apilib.android.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T : Any> Gson.fromJsonString(json: String): T {
    val type = object : TypeToken<T>() {}.type
    return fromJson(json, type)
}

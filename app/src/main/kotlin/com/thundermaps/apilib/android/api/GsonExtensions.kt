package com.thundermaps.apilib.android.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber

inline fun <reified T : Any> Gson.fromJsonString(json: String): T {
    val type = object : TypeToken<T>() {}.type
    Timber.e("type: ${T::class.simpleName}")
    Timber.e("json: $json")
    return fromJson<T>(json, type).also { Timber.e("result: $it") }
}

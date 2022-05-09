package com.thundermaps.apilib.android.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T : Any> Gson.fromJsonString(json: String): T {
    val type = object : TypeToken<T>() {}.type
    return fromJson(json, type)
}

@ExcludeFromJacocoGeneratedReport
fun <T> T.serializeToMap(gson: Gson): Map<String, Any> {
    return convert(gson)
}

@ExcludeFromJacocoGeneratedReport
inline fun <I, reified O> I.convert(gson: Gson): O {
    val json = gson.toJson(this)
    return gson.fromJson<O>(json, object : TypeToken<O>() {}.type)
}

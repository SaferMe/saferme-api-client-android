package com.thundermaps.apilib.android.api

import android.content.Context
import java.io.InputStreamReader

fun Context.readJsonFile(fileName: String): String =
    InputStreamReader(resources.assets.open(fileName)).readText()

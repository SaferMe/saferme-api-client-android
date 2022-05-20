package com.thundermaps.apilib.android.impl.resources

internal fun Map<String, String>?.toUriParameters(): String? = this?.keys?.joinToString("&") { "$it=${this[it]}" }

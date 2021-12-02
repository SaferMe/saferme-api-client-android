package com.thundermaps.apilib.android.impl

import java.util.UUID

internal object Provider {
    val xInstallId by lazy {
        UUID.randomUUID().toString()
    }
}

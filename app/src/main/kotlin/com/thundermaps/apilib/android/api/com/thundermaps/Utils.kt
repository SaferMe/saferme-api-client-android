package com.thundermaps.apilib.android.api.com.thundermaps

import java.net.InetAddress
import java.net.UnknownHostException

fun String?.isInternetAvailable(): Boolean {
    this?.let {
        try {
            val address: InetAddress = InetAddress.getByName(it)
            return !address.equals("")
        } catch (e: UnknownHostException) {
        }
    }
    return false
}

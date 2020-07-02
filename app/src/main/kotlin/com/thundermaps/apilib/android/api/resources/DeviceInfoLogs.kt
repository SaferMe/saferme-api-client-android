package com.thundermaps.apilib.android.api.resources

data class DeviceInfoLogs(
    val os_version: String = "",
    val device_model: String = "",
    val scan_timestamps: ArrayList<String> = ArrayList()
) : SaferMeDatum {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("os_version $os_version \n")
        sb.append("device_model $device_model \n")
        sb.append("scans: ")
        for (scan in scan_timestamps) {
            sb.append("$scan, \n")
        }
        return sb.toString()
    }
}

interface DeviceInfoLogsResource : Creatable<DeviceInfoLogs>

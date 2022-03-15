package com.thundermaps.apilib.android.api.resources

import com.google.gson.annotations.Expose

data class DeviceInfoLogs(
    @Expose
    val os_version: String = "",

    @Expose
    val device_model: String = "",

    @Expose
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

data class DeviceInfoLog(
    @Expose
    val device_info_log: DeviceInfoLogs
) : SaferMeDatum {
    override fun toString(): String {
        return device_info_log.toString()
    }
}
interface DeviceInfoLogsResource : Creatable<DeviceInfoLog>

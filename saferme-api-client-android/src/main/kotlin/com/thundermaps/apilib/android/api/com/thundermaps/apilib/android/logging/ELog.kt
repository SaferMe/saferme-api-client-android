package com.thundermaps.apilib.android.api.com.thundermaps.apilib.android.logging

import android.util.Log
import com.raygun.raygun4android.RaygunClient
import java.util.HashMap
import java.util.HashSet

class ELog {
    companion object {
        public val TAG = "SLog"
        val VERBOSE_LOGS_ENABLED = true
        val DEBUG_LOGS_ENABLED = true
        val INFO_LOGS_ENABLED = true
        val WARN_LOGS_ENABLED = true
        val ERROR_LOGS_ENABLED = true
        fun v(tag: String, msg: String) {
            if (VERBOSE_LOGS_ENABLED) Log.v(tag, msg)
        }

        fun d(tag: String, msg: String) {
            if (DEBUG_LOGS_ENABLED) Log.v(tag, msg)
        }

        fun i(tag: String, msg: String) {
            if (INFO_LOGS_ENABLED) Log.i(tag, msg)
        }

        fun w(tag: String, msg: String) {
            if (WARN_LOGS_ENABLED) Log.w(tag, msg)
        }

        fun e(e: SafermeException) {
            if (!ERROR_LOGS_ENABLED) return
            e.error.printStackTrace()
            try {
                if (!RaygunClient.isCrashReportingEnabled())
                    RaygunClient.enableCrashReporting()
                RaygunClient.send(e.error, e.tags, e.customData)
            } catch (exception: Exception) {
                try {
                    // according to our setup in thor cordova the raygun client may fail so we use this workarround to retry sending the logs
                    RaygunClient.send(
                        Exception("Raygun failed to send error", exception),
                        emptyList<Any>(),
                        emptyMap<Any, Any>()
                    )
                    RaygunClient.send(
                        Exception(e.error.toString()),
                        emptyList<Any>(),
                        emptyMap<Any, Any>()
                    )
                } catch (ex2: Exception) {
                    ex2.printStackTrace()
                }
            }
        }
    }
}

class SafermeException(val TAG: String, val message: String, val error: Throwable, val tags: List<ErrorTags>, val customData: HashMap<String, String>) {
    class Builder(
        var tag: String = ELog.TAG,
        var message: String = "Unnown Error",
        var th: Throwable = Exception("Unknown Error")
    ) {
        private val tags = HashSet<ErrorTags>()
        private var customData: HashMap<String, String> = HashMap()

        fun setTag(TAG: String): Builder {
            this.tag = TAG
            return this
        }

        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }

        fun setThrowable(th: Throwable): Builder {
            this.th = th
            return this
        }

        fun addTags(vararg args: ErrorTags): Builder {
            for (e in args) tags.add(e)
            return this
        }

        fun addCustomData(key: String, value: String): Builder {
            customData.put(key, value)
            return this
        }

        fun addCustomData(data: Map<String, String>): Builder {
            customData.putAll(data)
            return this
        }

        fun build(): SafermeException {
            customData.put("SLog_Tag", tag)
            customData.put("SLog_Message", message)
            val tagList = ArrayList(tags)
            return SafermeException(tag, message, th, tagList, customData)
        }
    }
}

enum class ErrorTags {
    SafermeApiLib
}

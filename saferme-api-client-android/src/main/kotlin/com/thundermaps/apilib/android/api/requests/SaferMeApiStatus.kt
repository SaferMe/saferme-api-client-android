package com.thundermaps.apilib.android.api.requests

enum class SaferMeApiStatus(private val code: Int?) {
    // Unknown
    UNKNOWN(0),

    // 100's
    OTHER_100(1),

    // 200's
    OK(200),
    CREATED(201),
    ACCEPTED(202),
    NO_CONTENT(204),
    OTHER_200(2),

    // 300's
    OTHER_300(3),

    // 400's
    OTHER_400(4),

    // 500's
    OTHER_500(5);

    companion object {
        fun statusForCode(code: Int): SaferMeApiStatus {
            return values().firstOrNull { v -> v.code == code }
            ?: when (code / 100) {
                1 -> OTHER_100
                2 -> OTHER_200
                3 -> OTHER_300
                4 -> OTHER_400
                5 -> OTHER_500
                else -> UNKNOWN
            }
        }
    }
}

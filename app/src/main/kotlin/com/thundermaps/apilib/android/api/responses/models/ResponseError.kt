package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.resources.SaferMeDatum

private const val ACCOUNT_LOCKED_CODE = "account_locked"

data class ResponseError(
    val errors: List<String>? = null,
    val failures: Failures? = null,
    @SerializedName("error_codes") val errorCodes: ErrorCodes? = null
) : SaferMeDatum

fun ResponseError.isAccountLocked() = ACCOUNT_LOCKED_CODE == errorCodes?.base?.firstOrNull()?.error

data class Failures(val base: List<String>)
data class ErrorCodes(val base: List<ErrorCode>)
data class ErrorCode(val error: String)

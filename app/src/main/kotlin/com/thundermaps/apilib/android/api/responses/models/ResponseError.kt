package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.resources.SaferMeDatum
import java.io.IOException

private const val ACCOUNT_LOCKED_CODE = "account_locked"

data class ResponseError(
    @Expose val errors: List<String>? = null,
    @Expose val failures: Failures? = null,
    @SerializedName("error_codes") @Expose val errorCodes: ErrorCodes? = null
) : SaferMeDatum

fun ResponseError.isAccountLocked() = ACCOUNT_LOCKED_CODE == errorCodes?.base?.firstOrNull()?.error

data class Failures(@Expose val base: List<String>)
data class ErrorCodes(@Expose val base: List<ErrorCode>)
data class ErrorCode(@Expose val error: String)

class ResponseException(
    @Expose val responseError: ResponseError
) : IOException()

package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport

@ExcludeFromJacocoGeneratedReport
data class PasswordRequirements(
    @SerializedName(value = MINIMUM_LENGTH) @Expose val minimumLength: Int,
    @SerializedName(value = STRENGTH_LEVELS) @Expose val strengthLevels: StrengthLevels
) {
    companion object {
        const val MINIMUM_LENGTH = "minimum_length"
        const val STRENGTH_LEVELS = "strength_levels"
    }
}

@ExcludeFromJacocoGeneratedReport
data class StrengthLevels(
    @Expose val weak: Int,
    @Expose val medium: Int,
    @Expose val strong: Int
)

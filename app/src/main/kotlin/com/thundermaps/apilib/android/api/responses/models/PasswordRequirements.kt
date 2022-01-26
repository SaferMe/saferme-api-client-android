package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.SerializedName

data class PasswordRequirements(
    @SerializedName(value = MINIMUM_LENGTH) val minimumLength: Int,
    @SerializedName(value = STRENGTH_LEVELS) val strengthLevels: StrengthLevels
) {
    companion object {
        const val MINIMUM_LENGTH = "minimum_length"
        const val STRENGTH_LEVELS = "strength_levels"
    }
}

data class StrengthLevels(
    val weak: Int,
    val medium: Int,
    val strong: Int
)

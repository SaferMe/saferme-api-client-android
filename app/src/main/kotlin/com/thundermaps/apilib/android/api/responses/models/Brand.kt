package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.SerializedName

data class Brand(
    @SerializedName(value = APP_ID) val appId: String,
    @SerializedName(value = ANDROID_URL) val appUrl: String,
    @SerializedName(value = DEFAULT_LOCATION) val defaultLocation: String,
    @SerializedName(value = DEFAULT_ZOOM) val defaultZoom: Float,
    @SerializedName(value = DISABLE_SIGN_UP) val disableSignUp: Boolean,
    val name: String,
    @SerializedName(value = ENABLE_LOCATION_WARNING) val enableLocationWarning: Boolean,
    @SerializedName(value = LATEST_VERSION) val latestVersion: String,
    @SerializedName(value = LAYER_TILE_JSON) val layerTitleJson: List<Layer>,
    @SerializedName(value = ONE_TAP_REPORTING) val isOneTapReporting: Boolean,
    @SerializedName(value = PASSWORD_REQUIREMENTS) val passwordRequirements: PasswordRequirements,
    @SerializedName(value = SAFETY_APP) val isSafetyApp: Boolean
) {
    companion object {
        const val ANDROID_URL = "android_url"
        const val APP_ID = "bundle_id"
        const val DEFAULT_LOCATION = "default_location"
        const val DEFAULT_ZOOM = "default_zoom"
        const val DISABLE_SIGN_UP = "disable_sign_up"
        const val ENABLE_LOCATION_WARNING = "enable_location_warning"
        const val LATEST_VERSION = "latest_version"
        const val LAYER_TILE_JSON = "layer_tilejson"
        const val ONE_TAP_REPORTING = "one_tap_reporting"
        const val PASSWORD_REQUIREMENTS = "password_requirements"
        const val SAFETY_APP = "safety_app"
    }
}

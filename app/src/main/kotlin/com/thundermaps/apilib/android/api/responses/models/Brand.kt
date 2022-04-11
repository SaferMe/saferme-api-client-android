package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport

@ExcludeFromJacocoGeneratedReport
data class Brand(
    @SerializedName(value = APP_ID) @Expose val appId: String,
    @SerializedName(value = ANDROID_URL) @Expose val appUrl: String,
    @SerializedName(value = DEFAULT_LOCATION) @Expose val defaultLocation: String,
    @SerializedName(value = DEFAULT_ZOOM) @Expose val defaultZoom: Float,
    @SerializedName(value = DISABLE_SIGN_UP) @Expose val disableSignUp: Boolean,
    @Expose val name: String,
    @SerializedName(value = ENABLE_LOCATION_WARNING) @Expose val enableLocationWarning: Boolean,
    @SerializedName(value = LATEST_VERSION) @Expose val latestVersion: String,
    @SerializedName(value = LAYER_TILE_JSON) @Expose val layerTitleJson: List<Layer>,
    @SerializedName(value = ONE_TAP_REPORTING) @Expose val isOneTapReporting: Boolean,
    @SerializedName(value = PASSWORD_REQUIREMENTS) @Expose val passwordRequirements: PasswordRequirements,
    @SerializedName(value = SAFETY_APP) @Expose val isSafetyApp: Boolean
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

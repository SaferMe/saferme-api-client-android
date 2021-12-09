package com.thundermaps.apilib.android.api.responses.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SsoDetails(
    @Expose val status: String,
    @SerializedName("sso_team_id") @Expose val ssoTeamId: String,
    @Expose val provider: String,
    @SerializedName("tenant_id") @Expose val tenantId: String,
    @SerializedName("client_id") @Expose val clientId: String,
    @SerializedName("sso_scope") @Expose val ssoScope: List<String>,
    @SerializedName("provider_base_url") @Expose val providerBaseUrl: String,
    @SerializedName("authorize_uri") @Expose val authorizeUri: String,
    @SerializedName("token_exchange_uri") @Expose val tokenExchangeUri: String
)

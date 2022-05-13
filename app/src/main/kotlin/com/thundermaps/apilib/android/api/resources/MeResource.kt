package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.models.EmailBody
import com.thundermaps.apilib.android.api.requests.models.UpdateAddressBody
import com.thundermaps.apilib.android.api.requests.models.UpdateContactNumberBody
import com.thundermaps.apilib.android.api.requests.models.UpdateEmailNotificationEnableBody
import com.thundermaps.apilib.android.api.requests.models.UpdateNameBody
import com.thundermaps.apilib.android.api.requests.models.UpdatePasswordBody
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.UserDetails

interface MeResource {
    suspend fun getUserDetails(parameters: RequestParameters, userId: String): Result<UserDetails>
    suspend fun updateAddress(
        parameters: RequestParameters,
        userId: String,
        addressBody: UpdateAddressBody
    ): Result<Unit>

    suspend fun updatePassword(
        parameters: RequestParameters,
        userId: String,
        updatePasswordBody: UpdatePasswordBody
    ): Result<Unit>

    suspend fun updateContactNumber(
        parameters: RequestParameters,
        userId: String,
        updateContactNumberBody: UpdateContactNumberBody
    ): Result<Unit>

    suspend fun updateEmail(
        parameters: RequestParameters,
        userId: String,
        emailBody: EmailBody
    ): Result<Unit>

    suspend fun updateName(
        parameters: RequestParameters,
        userId: String,
        updateNameBody: UpdateNameBody
    ): Result<Unit>

    suspend fun updateEmailNotificationEnabled(
        parameters: RequestParameters,
        userId: String,
        updateEmailNotificationEnableBody: UpdateEmailNotificationEnableBody
    ): Result<Unit>
}

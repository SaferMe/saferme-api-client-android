package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.models.EmailBody
import com.thundermaps.apilib.android.api.requests.models.UpdateAddressBody
import com.thundermaps.apilib.android.api.requests.models.UpdateContactNumberBody
import com.thundermaps.apilib.android.api.requests.models.UpdateEmailNotificationEnableBody
import com.thundermaps.apilib.android.api.requests.models.UpdateNameBody
import com.thundermaps.apilib.android.api.requests.models.UpdatePasswordBody
import com.thundermaps.apilib.android.api.requests.models.UpdateProfileBody
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.UserDetails

interface MeResource {
    suspend fun getUserDetails(parameters: RequestParameters): Result<UserDetails>
    suspend fun updateUserProfile(parameters: RequestParameters, userId: String, updateProfileBody: UpdateProfileBody): Result<Unit>
    suspend fun updateAddress(parameters: RequestParameters, addressBody: UpdateAddressBody): Result<Unit>
    suspend fun updatePassword(parameters: RequestParameters, updatePasswordBody: UpdatePasswordBody): Result<Unit>
    suspend fun updateContactNumber(parameters: RequestParameters, updateContactNumberBody: UpdateContactNumberBody): Result<Unit>
    suspend fun updateEmail(parameters: RequestParameters, emailBody: EmailBody): Result<Unit>
    suspend fun updateName(parameters: RequestParameters, updateNameBody: UpdateNameBody): Result<Unit>
    suspend fun updateEmailNotificationEnabled(parameters: RequestParameters, userId: String, updateEmailNotificationEnableBody: UpdateEmailNotificationEnableBody): Result<Unit>
}

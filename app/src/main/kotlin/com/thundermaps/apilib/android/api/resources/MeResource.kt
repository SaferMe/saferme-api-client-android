package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.models.UpdateAddressBody
import com.thundermaps.apilib.android.api.requests.models.UpdateContactNumberBody
import com.thundermaps.apilib.android.api.requests.models.UpdateEmailBody
import com.thundermaps.apilib.android.api.requests.models.UpdateNameBody
import com.thundermaps.apilib.android.api.requests.models.UpdatePasswordBody
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.UserDetails

interface MeResource {
    suspend fun getUserDetails(parameters: RequestParameters): Result<UserDetails>
    suspend fun updateAddress(parameters: RequestParameters, addressBody: UpdateAddressBody): Result<Unit>
    suspend fun updatePassword(parameters: RequestParameters, updatePasswordBody: UpdatePasswordBody): Result<Unit>
    suspend fun updateContactNumber(parameters: RequestParameters, updateContactNumberBody: UpdateContactNumberBody): Result<Unit>
    suspend fun updateEmail(parameters: RequestParameters, updateEmailBody: UpdateEmailBody): Result<Unit>
    suspend fun updateName(parameters: RequestParameters, updateNameBody: UpdateNameBody): Result<Unit>
}
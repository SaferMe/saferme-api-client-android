package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.responses.models.Brand
import com.thundermaps.apilib.android.api.responses.models.Result

interface BrandResource {
    suspend fun getBrand(applicationId: String): Result<Brand>
}

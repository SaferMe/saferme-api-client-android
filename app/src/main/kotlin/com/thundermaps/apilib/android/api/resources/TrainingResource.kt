package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.TrainingResponse

interface TrainingResource {
    suspend fun getTrainings(
        userId: String? = null,
        teamId: String? = null
    ): Result<List<TrainingResponse>>
}

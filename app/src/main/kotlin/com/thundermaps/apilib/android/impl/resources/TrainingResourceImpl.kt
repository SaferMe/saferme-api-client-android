package com.thundermaps.apilib.android.impl.resources

import com.thundermaps.apilib.android.api.resources.TrainingResource
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.TrainingResponse
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.apiRequest
import javax.inject.Inject

class TrainingResourceImpl @Inject constructor(
    private val androidClient: AndroidClient
) : TrainingResource {
    override suspend fun getTrainings(userId: String?, teamId: String?): Result<List<TrainingResponse>> {

        val parameters = mutableMapOf(
            "fields" to FIELDS.joinToString(",")
        ).apply {
            if (teamId != null) {
                this["team_id"] = teamId
            }
        }

        val (client, requestBuilder) = androidClient.buildRequest(
            parameters,
            PATH
        )
        return client.apiRequest(requestBuilder)
    }

    companion object {
        const val PATH = "training_records"
        val FIELDS = arrayOf("category", "competency_level_label", "competency_level_value", "completed_team_user_full_name", "module_name", "sign_off_team_user_full_name")
    }
}

package com.thundermaps.apilib.android.impl.resources

import android.util.Log
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiResult
import com.thundermaps.apilib.android.api.resources.Task
import com.thundermaps.apilib.android.api.resources.TaskResource
import com.thundermaps.apilib.android.impl.AndroidClient
import java.lang.IllegalArgumentException
import timber.log.Timber

class TaskResourceImpl(val api: AndroidClient) : TaskResource {

    override suspend fun create(
        parameters: RequestParameters,
        item: Task,
        success: (SaferMeApiResult<Task>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        StandardMethods.create(
            api = api,
            path = "tasks",
            parameters = parameters,
            item = item,
            success = success,
            failure = failure
        )
    }

    override suspend fun read(
        parameters: RequestParameters,
        item: Task,
        success: (SaferMeApiResult<Task>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        val uuid = item.uuid ?: throw IllegalArgumentException("Item MUST have a UUID")
        StandardMethods.read(
            api = api,
            path = "tasks/$uuid",
            parameters = parameters,
            success = success,
            failure = failure
        )
    }

    override suspend fun update(
        parameters: RequestParameters,
        item: Task,
        success: (SaferMeApiResult<Task>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        val uuid = item.uuid ?: throw IllegalArgumentException("Item MUST have a UUID")
        try {
            val jsonBody =
                if (item != null) AndroidClient.gsonSerializer.toJsonTree(item) else null
            if (jsonBody != null) Log.e("send-body update:", jsonBody.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        StandardMethods.update(
            api = api,
            path = "tasks/$uuid",
            parameters = parameters,
            item = item,
            success = success,
            failure = failure
        )
    }

    override suspend fun index(
        parameters: RequestParameters,
        success: (SaferMeApiResult<List<Task>>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        StandardMethods.index(
            api = api, path = "tasks", parameters = parameters, success = success, failure = failure
        )
    }

    override suspend fun delete(
        parameters: RequestParameters,
        identifier: Task,
        success: (SaferMeApiResult<Task>) -> Unit,
        failure: (Exception) -> Unit

    ) {
        val uuid = identifier.uuid ?: throw IllegalArgumentException("Item MUST have a UUID")
        try {
            val jsonBody =
                AndroidClient.gsonSerializer.toJsonTree(identifier)
            if (jsonBody != null) Timber.tag("send-body update:").e(jsonBody.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        StandardMethods.delete(
            api = api,
            path = "tasks/$uuid",
            parameters = parameters,
            success = success,
            failure = failure,
            item = identifier
        )
    }
}

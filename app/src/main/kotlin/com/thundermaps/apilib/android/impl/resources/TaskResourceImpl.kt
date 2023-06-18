package com.thundermaps.apilib.android.impl.resources

import com.google.gson.Gson
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiResult
import com.thundermaps.apilib.android.api.resources.DeletedResourceList
import com.thundermaps.apilib.android.api.resources.MarkAsIncomplete
import com.thundermaps.apilib.android.api.resources.Task
import com.thundermaps.apilib.android.api.resources.TaskResource
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.impl.AndroidClient
import com.thundermaps.apilib.android.impl.apiRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskResourceImpl @Inject constructor(
    val api: AndroidClient,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : TaskResource {

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

        val extensionParams = parameters.parameters?.toUriParameters()
        val path = extensionParams?.let { "$PATH?$it" } ?: PATH
        StandardMethods.index(
            api = api, path = path, parameters = parameters, success = success, failure = failure
        )
    }

    override suspend fun delete(
        parameters: RequestParameters,
        identifier: Task,
        success: (SaferMeApiResult<Task>) -> Unit,
        failure: (Exception) -> Unit

    ) {
        val uuid = identifier.uuid ?: throw IllegalArgumentException("Item MUST have a UUID")
        StandardMethods.delete(
            api = api,
            path = "$PATH/$uuid",
            parameters = parameters,
            success = success,
            failure = failure,
            item = identifier
        )
    }

    override suspend fun markAsInComplete(
        parameters: RequestParameters,
        uuid: String,
        success: (SaferMeApiResult<MarkAsIncomplete>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        val requestObject = MarkAsIncomplete(uuid, "", "")
        StandardMethods.update(
            api = api,
            path = "$PATH/$uuid",
            parameters = parameters,
            item = requestObject,
            success = success,
            failure = failure
        )
    }

    override suspend fun getTasksDeletedAfter(parameters: RequestParameters): Result<DeletedResourceList> {
        val params = parameters.copy(parameters = parameters.parameters ?: mapOf("type" to "task"))
        val (client, requestBuilder) = api.build(params, DELETED_TASK_PATH)
        return client.apiRequest(requestBuilder)
    }

    companion object {
        internal const val PATH = "tasks"
        internal const val DELETED_TASK_PATH = "deleted_resources"
    }
}

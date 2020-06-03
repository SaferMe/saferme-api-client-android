package com.thundermaps.apilib.android.api_impl.resources

import com.google.gson.reflect.TypeToken
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiResult
import com.thundermaps.apilib.android.api.resources.Task
import com.thundermaps.apilib.android.api.resources.TaskResource
import com.thundermaps.apilib.android.api_impl.AndroidClient
import java.lang.IllegalArgumentException

class TasksImpl(val api: AndroidClient): TaskResource {

    override suspend fun create(
        parameters: RequestParameters,
        item: Task,
        success: (SaferMeApiResult<Task>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        StandardMethods.create(
            api = api, path=  "tasks", parameters  = parameters, item = item, success= success, failure =  failure
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
            api = api, path = "tasks/$uuid", parameters = parameters, success = success, failure =  failure
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
            api = api, path=  "tasks/$uuid", parameters  = parameters, item = item, success= success, failure =  failure
        )
    }


    override suspend fun index(
        parameters: RequestParameters,
        success: (SaferMeApiResult<List<Task>>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        class TaskListToken : TypeToken<List<Task>>()
        StandardMethods.index(
            api = api, path=  "tasks", listType = TaskListToken(), parameters  = parameters, success= success, failure =  failure
        )
    }



    override suspend fun delete(
        parameters: RequestParameters,
        identifier: Task,
        success: (SaferMeApiResult<Task>) -> Unit,
        failure: (Exception) -> Unit

    ) {
        StandardMethods.delete(
            api = api, path=  "tasks", parameters  = parameters, success= success, failure =  failure, item = identifier
        )
    }

}


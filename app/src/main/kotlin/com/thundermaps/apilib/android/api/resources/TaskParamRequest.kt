package com.thundermaps.apilib.android.api.resources

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TaskParamRequest(
    @Expose
    @SerializedName("task")
    val tasks: Task
) : SaferMeDatum

interface TaskRequestResource : SaferMeResource<TaskParamRequest>,
    Creatable<TaskParamRequest>, Readable<TaskParamRequest>, Indexable<TaskParamRequest>, Updatable<TaskParamRequest>, Deletable<TaskParamRequest>

package com.thundermaps.apilib.android.api

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.TaskResource
import com.thundermaps.apilib.android.api.resources.TracedContactsResource

abstract class SaferMeClient {
    abstract val Tasks: TaskResource
    abstract fun defaultParams(): RequestParameters
    abstract val TracedContacts: TracedContactsResource
}

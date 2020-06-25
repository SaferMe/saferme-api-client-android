package com.thundermaps.apilib.android.api_impl.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiResult
import com.thundermaps.apilib.android.api.resources.TracedContacts
import com.thundermaps.apilib.android.api.resources.TracedContactsResource
import com.thundermaps.apilib.android.api_impl.AndroidClient

class TracedContactsImpl(val api: AndroidClient) : TracedContactsResource {

    override suspend fun create(
        parameters: RequestParameters,
        item: TracedContacts,
        success: (SaferMeApiResult<TracedContacts>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        StandardMethods.create(
            api = api, path =  "traced_contacts", parameters  = parameters, item = item, success= success, failure =  failure
        )
    }
}

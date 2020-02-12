package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.requests.SaferMeApiResult

/* SuperType for all API Resources */
interface SaferMeResource<T>

/* Implementors expose a create method */
interface Creatable<T : SaferMeDatum> : SaferMeResource<T> {
    suspend fun create(
        parameters: RequestParameters,
        item: T,
        success: (SaferMeApiResult<T>) -> Unit,
        failure: (Exception) -> Unit
    )
}

/* Implementors expose a read method. The identifier object must have a value for for its unique identifier */
interface Readable<T : SaferMeDatum> : SaferMeResource<T> {
    suspend fun read(
        parameters: RequestParameters,
        item: T,
        success: (SaferMeApiResult<T>) -> Unit,
        failure: (Exception) -> Unit
    )
}

/* Implementors expose an update method */
interface Updatable<T : SaferMeDatum> : SaferMeResource<T> {
    suspend fun update(
        parameters: RequestParameters,
        item: T,
        success: (SaferMeApiResult<T>) -> Unit,
        failure: (Exception) -> Unit
    )
}

/* Implementors expose a delete method. The identifier object must have a value for for its unique identifier */
interface Deletable<T : SaferMeDatum> : SaferMeResource<T> {
    suspend fun delete(
        parameters: RequestParameters,
        identifier: T,
        success: (SaferMeApiResult<List<T>>) -> Unit,
        failure: (Exception) -> Unit
    )
}

/* Implementors expose a create method */
interface Indexable<T : SaferMeDatum> : SaferMeResource<T> {
    suspend fun index(
        parameters: RequestParameters,
        success: (SaferMeApiResult<List<T>>) -> Unit,
        failure: (Exception) -> Unit
    )
}

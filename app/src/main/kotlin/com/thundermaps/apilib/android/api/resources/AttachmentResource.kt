package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.responses.models.FileAttachment
import com.thundermaps.apilib.android.api.responses.models.Result

interface AttachmentResource {
    suspend fun uploadFile(parameters: RequestParameters, filePath: String): Result<FileAttachment>
}

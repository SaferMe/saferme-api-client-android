package com.thundermaps.apilib.android.impl.resources

import android.net.Uri
import android.security.keystore.UserNotAuthenticatedException
import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.thundermaps.apilib.android.api.ExcludeFromJacocoGeneratedReport
import com.thundermaps.apilib.android.api.com.thundermaps.isInternetAvailable
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.AttachmentResource
import com.thundermaps.apilib.android.api.responses.models.FileAttachment
import com.thundermaps.apilib.android.api.responses.models.Result
import com.thundermaps.apilib.android.api.responses.models.ResultHandler
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormPart
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.util.KtorExperimentalAPI
import java.io.File
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@KtorExperimentalAPI
@Singleton
class AttachmentResourceImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : AttachmentResource {
    override suspend fun uploadFile(
        parameters: RequestParameters,
        filePath: String
    ): Result<FileAttachment> {
        if (!parameters.host.isInternetAvailable()) {
            return resultHandler.handleException(UnknownHostException())
        }
        val urlBuilder = AndroidClient.baseUrlBuilder(parameters)
        val (client, requestBuilder) = androidClient.client(parameters)
        val uploadAuthorizationResult = getUploadAuthentication(urlBuilder, client, requestBuilder)

        uploadAuthorizationResult.getNullableData()?.let { uploadAuthorization ->
            uploadFile(client, filePath, uploadAuthorization)
            return createFileAttachment(urlBuilder, client, requestBuilder, uploadAuthorization.key)
        }

        return resultHandler.handleException(UserNotAuthenticatedException("Error get upload authorization"))
    }

    private suspend fun uploadFile(
        client: HttpClient,
        filePath: String,
        uploadAuthorization: UploadAuthorization
    ) {
        val formData = formData {
            append(KEY, "${uploadAuthorization.key}/$IMAGE_FILE_NAME")
            append(
                AuthorizationFields.SUCCESS_ACTION_STATUS,
                uploadAuthorization.fields.successActionStatus
            )
            append(AuthorizationFields.CONTENT_TYPE, uploadAuthorization.fields.contentType)
            append(POLICY, uploadAuthorization.fields.policy)
            append(AuthorizationFields.X_AMZ_ALGORITHM, uploadAuthorization.fields.amzAlgorithm)
            append(AuthorizationFields.X_AMZ_DATE, uploadAuthorization.fields.amzDate)
            append(AuthorizationFields.X_AMZ_CREDENTIAL, uploadAuthorization.fields.amzCredential)
            append(AuthorizationFields.X_AMZ_SIGNATURE, uploadAuthorization.fields.amzSignature)
            append(FormPart(FILE, File(filePath).readBytes(), Headers.build {
                append(HttpHeaders.ContentDisposition, "${FileAttachment.FILE_NAME}=$IMAGE_FILE_NAME")
            }))
        }
        Uri.parse(uploadAuthorization.url).host?.let {
            client.submitFormWithBinaryData<Any>(
                host = it,
                formData = formData
            )
        }
    }

    private suspend fun getUploadAuthentication(
        urlBuilder: URLBuilder,
        client: HttpClient,
        requestBuilder: HttpRequestBuilder
    ): Result<UploadAuthorization> {
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = HttpMethod.Get
            url(urlBuilder.apply {
                encodedPath = "${encodedPath}$AUTHORIZATION_PATH?$CONTENT_TYPE_PARAMETER=$IMAGE_PNG"
            }.build())
        })
        return resultHandler.processResult(call, gson)
    }

    private suspend fun createFileAttachment(
        urlBuilder: URLBuilder,
        client: HttpClient,
        requestBuilder: HttpRequestBuilder,
        key: String
    ): Result<FileAttachment> {
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = HttpMethod.Post
            url(urlBuilder.apply {
                encodedPath = "${encodedPath}$FILE_ATTACHMENTS_PATH"
            }.build())
            contentType(ContentType.Application.Json)
            body = {
                FileAttachmentRequest(KeyRequest("$key/$IMAGE_FILE_NAME"))
            }
        })
        return resultHandler.processResult(call, gson)
    }

    companion object {
        @VisibleForTesting
        const val CONTENT_TYPE_PARAMETER = "content_type"
        const val IMAGE_PNG = "image/png"

        const val FILE_ATTACHMENTS_PATH = "file_attachments"
        @VisibleForTesting
        const val AUTHORIZATION_PATH = "file_attachments/upload_authorization"

        private const val KEY = "key"
        private const val POLICY = "policy"
        private const val FILE = "file"
        private const val IMAGE_FILE_NAME = "image.png"
    }
}

@ExcludeFromJacocoGeneratedReport
internal data class UploadAuthorization(
    val key: String,
    @SerializedName(value = KEY_PREFIX) val keyPrefix: String,
    val url: String,
    val fields: AuthorizationFields
) {
    companion object {
        const val KEY_PREFIX = "key_prefix"
    }
}

@ExcludeFromJacocoGeneratedReport
internal data class AuthorizationFields(
    val key: String,
    @SerializedName(value = SUCCESS_ACTION_STATUS) val successActionStatus: String,
    @SerializedName(value = CONTENT_TYPE) val contentType: String,
    val policy: String,
    @SerializedName(value = X_AMZ_CREDENTIAL) val amzCredential: String,
    @SerializedName(value = X_AMZ_ALGORITHM) val amzAlgorithm: String,
    @SerializedName(value = X_AMZ_DATE) val amzDate: String,
    @SerializedName(value = X_AMZ_SIGNATURE) val amzSignature: String
) {
    companion object {
        const val SUCCESS_ACTION_STATUS = "success_action_status"
        const val CONTENT_TYPE = "Content-Type"
        const val X_AMZ_CREDENTIAL = "x-amz-credential"
        const val X_AMZ_ALGORITHM = "x-amz-algorithm"
        const val X_AMZ_DATE = "x-amz-date"
        const val X_AMZ_SIGNATURE = "x-amz-signature"
    }
}

@ExcludeFromJacocoGeneratedReport
internal data class FileAttachmentRequest(
    @SerializedName(value = FILE_ATTACHMENT) val fileAttachment: KeyRequest
) {
    companion object {
        internal const val FILE_ATTACHMENT = "file_attachment"
    }
}

@ExcludeFromJacocoGeneratedReport
internal data class KeyRequest(val key: String)

package com.thundermaps.apilib.android.impl.resources

import android.security.keystore.UserNotAuthenticatedException
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.response.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.toByteArray
import java.io.File
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.io.errors.IOException
import timber.log.Timber

@KtorExperimentalAPI
@Singleton
class AttachmentResourceImpl @Inject constructor(
    private val androidClient: AndroidClient,
    private val resultHandler: ResultHandler,
    private val gson: Gson
) : AttachmentResource {
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    override suspend fun uploadFile(
        parameters: RequestParameters,
        filePath: String
    ): Result<FileAttachment> = coroutineScope {
        val scope = this
        if (!parameters.host.isInternetAvailable()) {
            return@coroutineScope resultHandler.handleException(UnknownHostException())
        }
        val urlBuilder = AndroidClient.baseUrlBuilder(parameters)
        val (client, requestBuilder) = androidClient.client(parameters)
        val uploadAuthorizationResult = getUploadAuthentication(urlBuilder, client, requestBuilder)

        uploadAuthorizationResult.getNullableData()?.let { response ->
            val uploadAuthorization = response.uploadAuthorization
            val uploadFileJob = scope.async {
                uploadFile(client, filePath, uploadAuthorization)
            }
            val createdFileAttachmentDeferred = scope.async {
                createFileAttachment(
                    urlBuilder,
                    client,
                    requestBuilder,
                    uploadAuthorization.keyPrefix
                )
            }
            uploadFileJob.await()
            return@coroutineScope createdFileAttachmentDeferred.await()
        }
        return@coroutineScope resultHandler.handleException(UserNotAuthenticatedException("Error get upload authorization"))
    }

    private suspend fun uploadFile(
        client: HttpClient,
        filePath: String,
        uploadAuthorization: UploadAuthorization
    ) {
        val formData = formData {
            append(KEY, "${uploadAuthorization.keyPrefix}/$IMAGE_FILE_NAME")
            val fields = uploadAuthorization.fields
            append(
                AuthorizationFields.SUCCESS_ACTION_STATUS,
                fields.successActionStatus
            )
            append(AuthorizationFields.CONTENT_TYPE, fields.contentType)
            append(POLICY, fields.policy)
            append(AuthorizationFields.X_AMZ_ALGORITHM, fields.amzAlgorithm)
            append(AuthorizationFields.X_AMZ_DATE, fields.amzDate)
            append(AuthorizationFields.X_AMZ_CREDENTIAL, fields.amzCredential)
            append(AuthorizationFields.X_AMZ_SIGNATURE, fields.amzSignature)

            val file = File(filePath)
            append(FILE, file.readBytes(), Headers.build {
                append(HttpHeaders.ContentType, IMAGE_PNG)
                append(
                    HttpHeaders.ContentDisposition,
                    "${FileAttachment.FILE_NAME}=$IMAGE_FILE_NAME"
                )
            })
        }
        Timber.e("uploadAuthorization: $uploadAuthorization")
        client.post<HttpResponse>(uploadAuthorization.url) {
            body = MultiPartFormDataContent(formData)
        }.use { response ->
            Timber.e("responseStatus: ${response.status.description}")
        }
    }

    suspend fun getUploadAuthentication(
        urlBuilder: URLBuilder,
        client: HttpClient,
        requestBuilder: HttpRequestBuilder
    ): Result<UploadAuthorizationResponse> {
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = HttpMethod.Get
            url(urlBuilder.apply {
                encodedPath =
                    "${encodedPath}$FILE_AUTHORIZATION_PATH?$CONTENT_TYPE_PARAMETER=$IMAGE_PNG"
            }.build())
        })
        val responseString = String(call.response.content.toByteArray())
        val adapter = moshi.adapter(UploadAuthorizationResponse::class.java)
        val data: UploadAuthorizationResponse? = try {
            adapter.fromJson(responseString)
        } catch (exception: IOException) {
            null
        }
        return data?.let { resultHandler.handleSuccess(it) } ?: resultHandler.handleException(
            Exception()
        )
    }

    private suspend fun createFileAttachment(
        urlBuilder: URLBuilder,
        client: HttpClient,
        requestBuilder: HttpRequestBuilder,
        keyPrefix: String
    ): Result<FileAttachment> {
        val call = client.call(HttpRequestBuilder().takeFrom(requestBuilder).apply {
            method = HttpMethod.Post
            url(urlBuilder.apply {
                encodedPath = "${encodedPath}$FILE_ATTACHMENTS_PATH"
            }.build())
            contentType(ContentType.Application.Json)
            body = {
                FileAttachmentRequest(KeyRequest("$keyPrefix$IMAGE_FILE_NAME"))
            }
        })
        return resultHandler.processResult(call, gson)
    }

    companion object {
        private const val CONTENT_TYPE_PARAMETER = "content_type"
        private const val IMAGE_PNG = "image/png"
        private const val FILE_ATTACHMENTS_PATH = "file_attachments"
        private const val FILE_AUTHORIZATION_PATH = "file_attachments/upload_authorization"

        private const val KEY = "key"
        private const val POLICY = "policy"
        private const val FILE = "file"
        private const val IMAGE_FILE_NAME = "image.png"
    }
}

@JsonClass(generateAdapter = true)
data class UploadAuthorizationResponse(
    @Json(name = "upload_authorization") @Expose val uploadAuthorization: UploadAuthorization
)

@JsonClass(generateAdapter = true)
data class UploadAuthorization(
    @Expose val key: String,
    @Json(name = KEY_PREFIX) @Expose val keyPrefix: String,
    @Expose val url: String,
    @Expose val fields: AuthorizationFields
) {
    companion object {
        const val KEY_PREFIX = "key_prefix"
    }
}

@JsonClass(generateAdapter = true)
data class AuthorizationFields(
    @Expose val key: String,
    @Json(name = SUCCESS_ACTION_STATUS) @Expose val successActionStatus: String,
    @Json(name = CONTENT_TYPE) @Expose val contentType: String,
    @Expose val policy: String,
    @Json(name = X_AMZ_CREDENTIAL) @Expose val amzCredential: String,
    @Json(name = X_AMZ_ALGORITHM) @Expose val amzAlgorithm: String,
    @Json(name = X_AMZ_DATE) @Expose val amzDate: String,
    @Json(name = X_AMZ_SIGNATURE) @Expose val amzSignature: String
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
    @SerializedName(value = FILE_ATTACHMENT) @Expose val fileAttachment: KeyRequest
) {
    companion object {
        internal const val FILE_ATTACHMENT = "file_attachment"
    }
}

@ExcludeFromJacocoGeneratedReport
internal data class KeyRequest(val key: String)

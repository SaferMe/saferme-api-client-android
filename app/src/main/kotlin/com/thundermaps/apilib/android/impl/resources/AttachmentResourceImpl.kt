package com.thundermaps.apilib.android.impl.resources

import android.security.keystore.UserNotAuthenticatedException
import androidx.annotation.VisibleForTesting
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
import com.thundermaps.apilib.android.api.serializeToMap
import com.thundermaps.apilib.android.impl.AndroidClient
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.coroutineScope
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
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @ExcludeFromJacocoGeneratedReport
    override suspend fun uploadFile(
        parameters: RequestParameters,
        filePath: String
    ): Result<FileAttachment> = coroutineScope {
        val scope = this
        if (!parameters.host.isInternetAvailable()) {
            return@coroutineScope resultHandler.handleException(UnknownHostException())
        }
        val (client, requestBuilder) = androidClient.client(parameters)
        val uploadAuthorizationResult = getUploadAuthentication(parameters, client, requestBuilder)

        uploadAuthorizationResult.getNullableData()?.let { response ->
            val uploadAuthorization = response.uploadAuthorization
            if (uploadFile(client, filePath, uploadAuthorization)) {
                return@coroutineScope createFileAttachment(
                    parameters,
                    client,
                    requestBuilder,
                    uploadAuthorization.keyPrefix
                )
            }
        }
        return@coroutineScope resultHandler.handleException(UserNotAuthenticatedException("Error upload file"))
    }

    @OptIn(InternalAPI::class)
    @ExcludeFromJacocoGeneratedReport
    private suspend fun uploadFile(
        client: HttpClient,
        filePath: String,
        uploadAuthorization: UploadAuthorization
    ): Boolean {
        val formData = formData {
            val fields = uploadAuthorization.fields.copy(key = "${uploadAuthorization.keyPrefix}$IMAGE_FILE_NAME")
            val map = fields.serializeToMap(gson)

            map.forEach {
                append(it.key, it.value)
            }

            val file = File(filePath)
            append(
                FILE, file.readBytes(),
                Headers.build {
                    append(HttpHeaders.ContentType, IMAGE_PNG)
                    append(
                        HttpHeaders.ContentDisposition,
                        "${FileAttachment.FILE_NAME}=$IMAGE_FILE_NAME"
                    )
                }
            )
        }
        return client.submitFormWithBinaryData<HttpResponse>(
            url = uploadAuthorization.url,
            formData = formData
        ).status == HttpStatusCode.Created
    }

    suspend fun getUploadAuthentication(
        parameters: RequestParameters,
        client: HttpClient,
        requestBuilder: HttpRequestBuilder
    ): Result<UploadAuthorizationResponse> {
        val call = client.request<HttpResponse> (
            HttpRequestBuilder().takeFrom(requestBuilder).apply {
                method = HttpMethod.Get
                url(
                    AndroidClient.baseUrlBuilder(parameters).apply {
                        encodedPath =
                            "$encodedPath$FILE_AUTHORIZATION_PATH?$CONTENT_TYPE_PARAMETER=$IMAGE_PNG"
                    }.build()
                )
            }
        ).call
        return resultHandler.processResult(call, moshi, gson)
    }

    @VisibleForTesting
    suspend fun createFileAttachment(
        parameters: RequestParameters,
        client: HttpClient,
        requestBuilder: HttpRequestBuilder,
        keyPrefix: String
    ): Result<FileAttachment> {
        val fileAttachmentBody = FileAttachmentRequest(KeyRequest("$keyPrefix$IMAGE_FILE_NAME"))
        val call = client.request<HttpResponse> (
            HttpRequestBuilder().takeFrom(requestBuilder).apply {
                method = HttpMethod.Post
                url(
                    AndroidClient.baseUrlBuilder(parameters).apply {
                        encodedPath = "$encodedPath$FILE_ATTACHMENTS_PATH"
                    }.build()
                )

                contentType(ContentType.Application.Json)
                body = fileAttachmentBody
            }
        ).call
        return resultHandler.processResult(call, gson)
    }

    companion object {
        private const val CONTENT_TYPE_PARAMETER = "content_type"
        private const val IMAGE_PNG = "image/png"
        private const val FILE_ATTACHMENTS_PATH = "file_attachments"
        private const val FILE_AUTHORIZATION_PATH = "file_attachments/upload_authorization"
        private const val FILE = "file"
        private const val IMAGE_FILE_NAME = "image.png"
    }
}

@JsonClass(generateAdapter = true)
data class UploadAuthorizationResponse(
    @Json(name = "upload_authorization") val uploadAuthorization: UploadAuthorization
)

@JsonClass(generateAdapter = true)
data class UploadAuthorization(
    val key: String,
    @Json(name = KEY_PREFIX) val keyPrefix: String,
    val url: String,
    val fields: AuthorizationFields
) {
    companion object {
        const val KEY_PREFIX = "key_prefix"
    }
}

@JsonClass(generateAdapter = true)
data class AuthorizationFields(
    @SerializedName(KEY) @Expose val key: String,
    @Json(name = SUCCESS_ACTION_STATUS) @SerializedName(SUCCESS_ACTION_STATUS) @Expose val successActionStatus: String,
    @Json(name = CONTENT_TYPE) @SerializedName(CONTENT_TYPE) @Expose val contentType: String,
    @SerializedName(POLICY) @Expose val policy: String,
    @Json(name = X_AMZ_CREDENTIAL) @SerializedName(X_AMZ_CREDENTIAL) @Expose val amzCredential: String,
    @Json(name = X_AMZ_ALGORITHM) @SerializedName(X_AMZ_ALGORITHM) @Expose val amzAlgorithm: String,
    @Json(name = X_AMZ_DATE) @SerializedName(X_AMZ_DATE) @Expose val amzDate: String,
    @Json(name = X_AMZ_SIGNATURE) @SerializedName(X_AMZ_SIGNATURE) @Expose val amzSignature: String,
    @Json(name = X_AMZ_SECURITY_TOKEN) @SerializedName(X_AMZ_SECURITY_TOKEN) @Expose val amzSecurityToken: String
) {
    companion object {
        const val SUCCESS_ACTION_STATUS = "success_action_status"
        const val CONTENT_TYPE = "Content-Type"
        const val X_AMZ_CREDENTIAL = "x-amz-credential"
        const val X_AMZ_ALGORITHM = "x-amz-algorithm"
        const val X_AMZ_DATE = "x-amz-date"
        private const val KEY = "key"
        private const val POLICY = "policy"
        const val X_AMZ_SIGNATURE = "x-amz-signature"
        const val X_AMZ_SECURITY_TOKEN = "x-amz-security-token"
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
internal data class KeyRequest(@SerializedName(value = "key") @Expose val key: String)

package com.thundermaps.apilib.android.impl

import com.thundermaps.apilib.android.api.SaferMeClient
import com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager
import com.thundermaps.apilib.android.api.requests.RequestParameters
import com.thundermaps.apilib.android.api.resources.AttachmentResource
import com.thundermaps.apilib.android.api.resources.BrandResource
import com.thundermaps.apilib.android.api.resources.CategoryResource
import com.thundermaps.apilib.android.api.resources.ChannelResource
import com.thundermaps.apilib.android.api.resources.DeviceInfoLogsResource
import com.thundermaps.apilib.android.api.resources.FormResource
import com.thundermaps.apilib.android.api.resources.MeResource
import com.thundermaps.apilib.android.api.resources.ReportResource
import com.thundermaps.apilib.android.api.resources.ReportStateChangeResource
import com.thundermaps.apilib.android.api.resources.RiskResource
import com.thundermaps.apilib.android.api.resources.SessionsResource
import com.thundermaps.apilib.android.api.resources.ShapeResource
import com.thundermaps.apilib.android.api.resources.StateResource
import com.thundermaps.apilib.android.api.resources.TaskResource
import com.thundermaps.apilib.android.api.resources.TeamResource
import com.thundermaps.apilib.android.api.resources.TracedContactsResource
import com.thundermaps.apilib.android.api.resources.TrainingResource
import com.thundermaps.apilib.android.impl.resources.AttachmentResourceImpl
import com.thundermaps.apilib.android.impl.resources.BrandResourceImpl
import com.thundermaps.apilib.android.impl.resources.CategoryResourceImpl
import com.thundermaps.apilib.android.impl.resources.ChannelResourceImpl
import com.thundermaps.apilib.android.impl.resources.DeviceInfoLogsImpl
import com.thundermaps.apilib.android.impl.resources.FormResourceImpl
import com.thundermaps.apilib.android.impl.resources.MeResourceImpl
import com.thundermaps.apilib.android.impl.resources.ReportImpl
import com.thundermaps.apilib.android.impl.resources.ReportStateChangeResourceImpl
import com.thundermaps.apilib.android.impl.resources.RiskResourceImpl
import com.thundermaps.apilib.android.impl.resources.SessionsImpl
import com.thundermaps.apilib.android.impl.resources.ShapeResourceImpl
import com.thundermaps.apilib.android.impl.resources.StateImpl
import com.thundermaps.apilib.android.impl.resources.TaskResourceImpl
import com.thundermaps.apilib.android.impl.resources.TeamResourceImpl
import com.thundermaps.apilib.android.impl.resources.TracedContactsImpl
import com.thundermaps.apilib.android.impl.resources.TrainingResourceImpl
import io.ktor.util.KtorExperimentalAPI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(KtorExperimentalAPI::class)
class SaferMeClientImpl @Inject constructor(
    private val androidClient: AndroidClient,
    override val environmentManager: EnvironmentManager,
    private val teamResourceImpl: TeamResourceImpl,
    private val meResourceImpl: MeResourceImpl,
    private val sessionsImpl: SessionsImpl,
    private val channelImpl: ChannelResourceImpl,
    private val brandResourceImpl: BrandResourceImpl,
    private val shapeResourceImpl: ShapeResourceImpl,
    private val categoryResourceImpl: CategoryResourceImpl,
    private val formResourceImpl: FormResourceImpl,
    private val attachmentResourceImpl: AttachmentResourceImpl,
    private val stateImpl: StateImpl,
    private val reportImpl: ReportImpl,
    private val taskResourceImpl: TaskResourceImpl,
    private val reportStateChangeResourceImpl: ReportStateChangeResourceImpl,
    private val riskResourceImpl: RiskResourceImpl,
    private val trainingResourceImpl: TrainingResourceImpl
) : SaferMeClient {
    override val taskResource: TaskResource get() = taskResourceImpl

    override val reportResource: ReportResource get() = reportImpl

    override val tracedContacts: TracedContactsResource = TracedContactsImpl(androidClient)

    override val deviceInfoLogs: DeviceInfoLogsResource = DeviceInfoLogsImpl(androidClient)

    override val sessionsResource: SessionsResource
        get() = sessionsImpl

    override val teamResource: TeamResource
        get() = teamResourceImpl

    override val meResource: MeResource
        get() = meResourceImpl

    override val channelResource: ChannelResource
        get() = channelImpl

    override val brandResource: BrandResource
        get() = brandResourceImpl

    override val shapeResource: ShapeResource
        get() = shapeResourceImpl

    override val categoryResource: CategoryResource
        get() = categoryResourceImpl

    override val formResource: FormResource
        get() = formResourceImpl

    override val attachmentResource: AttachmentResource
        get() = attachmentResourceImpl

    override val stateResource: StateResource
        get() = stateImpl

    override val reportStateChangeResource: ReportStateChangeResource
        get() = reportStateChangeResourceImpl

    override val riskResource: RiskResource
        get() = riskResourceImpl

    override val trainingResource: TrainingResource
        get() = trainingResourceImpl

    override fun defaultParams(): RequestParameters = RequestParameters(
        customRequestHeaders = HashMap(),
        credentials = null,
        host = DEFAULT_API_ENDPOINT,
        port = null,
        api_version = 4
    )

    companion object {
        // Constants
        private const val DEFAULT_API_ENDPOINT = "public-api.thundermaps.com"
    }
}

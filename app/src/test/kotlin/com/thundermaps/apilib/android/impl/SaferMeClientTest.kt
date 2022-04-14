package com.thundermaps.apilib.android.impl

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.thundermaps.apilib.android.api.SaferMeClient
import com.thundermaps.apilib.android.api.com.thundermaps.env.EnvironmentManager
import com.thundermaps.apilib.android.api.com.thundermaps.env.Staging
import com.thundermaps.apilib.android.impl.resources.BrandResourceImpl
import com.thundermaps.apilib.android.impl.resources.CategoryResourceImpl
import com.thundermaps.apilib.android.impl.resources.ChannelResourceImpl
import com.thundermaps.apilib.android.impl.resources.DeviceInfoLogsImpl
import com.thundermaps.apilib.android.impl.resources.FormResourceImpl
import com.thundermaps.apilib.android.impl.resources.MeResourceImpl
import com.thundermaps.apilib.android.impl.resources.ReportImpl
import com.thundermaps.apilib.android.impl.resources.SessionsImpl
import com.thundermaps.apilib.android.impl.resources.ShapeResourceImpl
import com.thundermaps.apilib.android.impl.resources.TasksImpl
import com.thundermaps.apilib.android.impl.resources.TeamResourceImpl
import com.thundermaps.apilib.android.impl.resources.TeamUsersResourceImpl
import com.thundermaps.apilib.android.impl.resources.TracedContactsImpl
import io.ktor.util.KtorExperimentalAPI
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@KtorExperimentalAPI
internal class SaferMeClientTest {
    private val androidClient = AndroidClient()
    private val environmentManager: EnvironmentManager = mock {
        on { environment } doReturn Staging
    }
    private val teamResourceImpl = mock<TeamResourceImpl>()
    private val meResourceImpl = mock<MeResourceImpl>()
    private val sessionsImpl = mock<SessionsImpl>()
    private val channelImpl = mock<ChannelResourceImpl>()
    private val brandResourceImpl = mock<BrandResourceImpl>()
    private val shapeResourceImpl = mock<ShapeResourceImpl>()
    private val categoryResourceImpl = mock<CategoryResourceImpl>()
    private val formResourceImpl = mock<FormResourceImpl>()
    private val teamUserResourceImpl = mock<TeamUsersResourceImpl>()
    private lateinit var saferMeClient: SaferMeClient

    @Before
    fun setUp() {
        saferMeClient = SaferMeClientImpl(
            androidClient,
            environmentManager,
            teamResourceImpl,
            meResourceImpl,
            sessionsImpl,
            channelImpl,
            brandResourceImpl,
            shapeResourceImpl,
            categoryResourceImpl,
            formResourceImpl,
            teamUserResourceImpl
        )
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(
            environmentManager,
            teamResourceImpl,
            meResourceImpl,
            sessionsImpl,
            channelImpl,
            brandResourceImpl,
            formResourceImpl
        )
    }

    @Test
    fun verifyDefaultParams() {
        val params = saferMeClient.defaultParams()
        assertEquals(0, params.customRequestHeaders.size)
        assertNull(params.credentials)
        assertEquals("public-api.thundermaps.com", params.host)
        assertNull(params.port)
        assertEquals(4, params.api_version)
    }

    @Test
    fun verifyTaskResource() {
        val taskResource = saferMeClient.taskResource
        assertNotNull(taskResource)
        assertTrue(taskResource is TasksImpl)
        assertEquals(androidClient, (taskResource as TasksImpl).api)
    }

    @Test
    fun verifyReportResource() {
        val reportResource = saferMeClient.reportResource
        assertNotNull(reportResource)
        assertTrue(reportResource is ReportImpl)
        assertEquals(androidClient, (reportResource as ReportImpl).api)
    }

    @Test
    fun verifyTracedContacts() {
        val tracedContacts = saferMeClient.tracedContacts
        assertNotNull(tracedContacts)
        assertTrue(tracedContacts is TracedContactsImpl)
        assertEquals(androidClient, (tracedContacts as TracedContactsImpl).api)
    }

    @Test
    fun verifyDeviceInfoLogs() {
        val deviceInfoLogs = saferMeClient.deviceInfoLogs
        assertNotNull(deviceInfoLogs)
        assertTrue(deviceInfoLogs is DeviceInfoLogsImpl)
        assertEquals(androidClient, (deviceInfoLogs as DeviceInfoLogsImpl).api)
    }

    @Test
    fun verifySessionsResource() {
        val sessionsResource = saferMeClient.sessionsResource
        assertNotNull(sessionsResource)
        assertEquals(sessionsImpl, sessionsResource)
    }

    @Test
    fun verifyTeamResource() {
        val teamResource = saferMeClient.teamResource
        assertNotNull(teamResource)
        assertEquals(teamResourceImpl, teamResource)
    }

    @Test
    fun verifyChannelResource() {
        val channelResource = saferMeClient.channelResource
        assertNotNull(channelResource)
        assertEquals(channelImpl, channelResource)
    }

    @Test
    fun verifyMeResource() {
        val meResource = saferMeClient.meResource
        assertNotNull(meResource)
        assertEquals(meResourceImpl, meResource)
    }

    @Test
    fun verifyBrandResource() {
        val brandResource = saferMeClient.brandResource
        assertNotNull(brandResource)
        assertEquals(brandResourceImpl, brandResource)
    }

    @Test
    fun verifyShapeResource() {
        val shapeResource = saferMeClient.shapeResource
        assertNotNull(shapeResource)
        assertEquals(shapeResourceImpl, shapeResource)
    }

    @Test
    fun verifyCategoryResource() {
        val categoryResource = saferMeClient.categoryResource
        assertNotNull(categoryResource)
        assertEquals(categoryResourceImpl, categoryResource)
    }

    @Test
    fun verifyFormResource() {
        val formResource = saferMeClient.formResource

        assertNotNull(formResource)
        assertEquals(formResourceImpl, formResource)
    }

    @Test
    fun verifyEnvironmentManager() {
        assertEquals(environmentManager, saferMeClient.environmentManager)
    }
}

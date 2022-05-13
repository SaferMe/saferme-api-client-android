package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.responses.models.ReportState

interface StateResource: SaferMeResource<ReportState>, Readable<ReportState>, Indexable<ReportState>

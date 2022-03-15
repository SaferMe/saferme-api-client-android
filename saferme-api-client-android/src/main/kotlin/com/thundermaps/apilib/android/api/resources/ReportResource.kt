package com.thundermaps.apilib.android.api.resources

import com.thundermaps.apilib.android.api.responses.models.Report

interface ReportResource : SaferMeResource<Report>,
    Creatable<Report>, Readable<Report>, Indexable<Report>, Updatable<Report>, Deletable<Report>

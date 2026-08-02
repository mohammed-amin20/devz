package com.mohamed.devz.feature.core.domain.repository

import com.mohamed.devz.feature.core.domain.model.Report
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result

interface ReportRepository {
    suspend fun insert(report: Report): Result<Report, Error>
    suspend fun getAll(status: String? = null): Result<List<Report>, Error>
    suspend fun getByReporterAndTarget(
        reporterId: Int,
        reportedType: String,
        reportedId: Int,
    ): Result<Report?, Error>
    suspend fun updateStatus(id: Int, status: String): Result<Unit, Error>
}

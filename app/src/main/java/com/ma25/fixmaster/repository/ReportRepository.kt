package com.ma25.fixmaster.repository

import com.ma25.fixmaster.data.model.IssueReport
import kotlinx.coroutines.flow.Flow

interface ReportRepository {

    fun observeReports(): Flow<List<IssueReport>>

    suspend fun addReport(report: IssueReport)

    suspend fun updateReportStatus(
        reportId: String,
        newStatus: String
    )
}
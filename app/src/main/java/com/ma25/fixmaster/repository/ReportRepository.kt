package com.ma25.fixmaster.repository

import com.ma25.fixmaster.data.model.IssueReport
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    suspend fun addReport(report: IssueReport)

    fun observeReports(): Flow<List<IssueReport>>          // Admin list (not Klar)
    fun observeMyReports(uid: String): Flow<List<IssueReport>> // User list (createdBy = uid)

    suspend fun updateReportStatus(reportId: String, newStatus: String)
}
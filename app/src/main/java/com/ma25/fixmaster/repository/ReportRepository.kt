package com.ma25.fixmaster.repository

import com.ma25.fixmaster.model.IssueReport
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    suspend fun addReport(report: IssueReport)

    fun observeReports(): Flow<List<IssueReport>>               // Admin list
    fun observeMyReports(uid: String): Flow<List<IssueReport>>  // User list

    suspend fun updateReport(reportId: String, newStatus: String, newPriority: String, adminComment: String?)
}
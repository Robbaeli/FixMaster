package com.ma25.fixmaster.repository

import com.ma25.fixmaster.model.Report
import kotlinx.coroutines.flow.Flow

interface ReportRepository {

    fun observeReports(): Flow<List<Report>>

    suspend fun addReport(report: Report)
}
package com.ma25.fixmaster.repository


import com.ma25.fixmaster.model.Report
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryReportRepository: ReportRepository {



    private val reports = mutableListOf<Report>(
        Report("1",
            "Kaffemaskin 3",
            "Våning 2",
            "Trasig",
        ),
        Report("2",
            "Projektor",
            "Våning 1",
            "Trasig",
        )
    )

private val reportsFlow = MutableStateFlow(reports.toList())

    override fun observeReports(): Flow<List<Report>> {
        return reportsFlow.asStateFlow()
    }

    override suspend fun addReport(report: Report) {
        reports.add(report)
        reportsFlow.value = reports.toList()
    }
}

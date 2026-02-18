package com.ma25.fixmaster.repository

import com.ma25.fixmaster.model.Report
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object FakeReportRepository {

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

    fun addReport(report: Report) {
        _reports.update { current -> current + report }
    }

    fun clear() {
        _reports.value = emptyList()
    }
}

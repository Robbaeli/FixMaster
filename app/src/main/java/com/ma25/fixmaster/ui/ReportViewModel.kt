package com.ma25.fixmaster.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma25.fixmaster.model.Report
import com.ma25.fixmaster.repository.FakeReportRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {

    private val _state = MutableStateFlow<ReportState>(ReportState.Idle)
    val state: StateFlow<ReportState> = _state

    fun submitReport(
        objectId: String,
        objectName: String,
        faultType: String,
        userId: String? = null
    ) {

        if (faultType.isBlank()) {
            _state.value = ReportState.Error("Välj en feltyp.")
            return
        }

        viewModelScope.launch {

            _state.value = ReportState.Loading

            try {
                // Simulera nätverk / backend
                delay(800)

                val report = Report(
                    objectId = objectId,
                    objectName = objectName,
                    faultType = faultType,
                    userId = userId
                )

                FakeReportRepository.addReport(report)

                _state.value = ReportState.Success

            } catch (e: Exception) {
                _state.value = ReportState.Error("Något gick fel.")
            }
        }
    }
}


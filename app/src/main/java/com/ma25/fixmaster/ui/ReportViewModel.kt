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

    fun submitReport(objectId: String, objectName: String, faultType: String) {
        _state.value = ReportState.Loading

        viewModelScope.launch {
            try {
                // Simulera nätverksfördröjning
                delay(1000)

                val newReport = Report(
                    objectId = objectId,
                    objectName = objectName,
                    faultType = faultType
                )

                // Här kan vi  byta till ObjectRepository om vi  vill köra Firebase tex vid att skapa manuellt objekt
                FakeReportRepository.addReport(newReport)

                _state.value = ReportState.Success
            } catch (e: Exception) {
                _state.value = ReportState.Error("Kunde inte skicka rapporten.")
            }
        }
    }
}
package com.ma25.fixmaster.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma25.fixmaster.data.model.IssueReport
import com.ma25.fixmaster.repository.ObjectRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {

    private val repository = ObjectRepository()


    private val _state = MutableStateFlow<ReportState>(ReportState.Idle)
    val state: StateFlow<ReportState> = _state

    fun submitReport(objectId: String, objectName: String, faultType: String ,createdBy: String) {
        _state.value = ReportState.Loading

        viewModelScope.launch {
            try {
                // Simulera nätverksfördröjning
                delay(1000)

                val newReport = IssueReport(
                    objectId = objectId,
                    objectName = objectName,
                    description = faultType,
                    status = "Ny",
                    createdBy = createdBy
                )

                // Här kan vi  byta till ObjectRepository om vi  vill köra Firebase tex vid att skapa manuellt objekt
                //Micke - La till ObjectRepository
                repository.addReport(newReport)

                _state.value = ReportState.Success
            } catch (e: Exception) {
                _state.value = ReportState.Error("Kunde inte skicka rapporten.")
            }
        }
    }
}
package com.ma25.fixmaster.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma25.fixmaster.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportDetailViewModel (
    private val repository: ReportRepository
) : ViewModel() {

    private val _state = MutableStateFlow<DetailState>(DetailState.Idle)
    val state: StateFlow<DetailState> = _state

    fun updateStatus (reportId: String, newStatus: String) {
        viewModelScope.launch {
            _state.value = DetailState.Loading
            try {
                repository.updateReportStatus(reportId, newStatus)
                _state.value = DetailState.Success
            } catch (e: Exception) {
                _state.value = DetailState.Error(
                    e.message ?: "Unknown error"
                )
            }
        }
    }

}
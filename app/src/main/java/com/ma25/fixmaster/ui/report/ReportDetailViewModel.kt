package com.ma25.fixmaster.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma25.fixmaster.repository.ReportRepository
import com.ma25.fixmaster.ui.report.state.DetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportDetailViewModel(
    private val repository: ReportRepository
) : ViewModel() {

    private val _state = MutableStateFlow<DetailState>(DetailState.Idle)
    val state: StateFlow<DetailState> = _state

    fun updateReport(reportId: String, newStatus: String, newPriority: String, adminComment: String?) {
        viewModelScope.launch {
            _state.value = DetailState.Loading
            try {
                repository.updateReport(reportId, newStatus, newPriority, adminComment)
                _state.value = DetailState.Success
            } catch (e: Exception) {
                _state.value = DetailState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
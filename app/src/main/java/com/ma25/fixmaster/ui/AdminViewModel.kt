package com.ma25.fixmaster.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma25.fixmaster.repository.ObjectRepository
import com.ma25.fixmaster.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val repository: ReportRepository = ObjectRepository()

    private val _state = MutableStateFlow<AdminState>(AdminState.Loading)
    val state: StateFlow<AdminState> = _state.asStateFlow()


    fun loadReports() {
        viewModelScope.launch {
            _state.value = AdminState.Loading

            repository.observeReports().collect { reports ->
                _state.value = AdminState.Success(reports)
            }
        }
    }
}
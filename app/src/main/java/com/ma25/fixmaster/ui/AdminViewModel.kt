package com.ma25.fixmaster.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma25.fixmaster.model.AdminFilter
import com.ma25.fixmaster.repository.ObjectRepository
import com.ma25.fixmaster.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val repository: ReportRepository = ObjectRepository()

    private val _state = MutableStateFlow<AdminState>(AdminState.Loading)

    private val selectedFilter = MutableStateFlow(AdminFilter.ALL)

    val state: StateFlow<AdminState> = _state.asStateFlow()

    fun setFilter(filter: AdminFilter) {
        selectedFilter.value = filter
    }


    fun loadReports() {
        viewModelScope.launch {

            repository.observeReports()
                .combine(selectedFilter) { reports, filter ->
                    val filtered = when (filter) {
                        AdminFilter.ALL -> reports

                        AdminFilter.HIGH_PRIORITY ->
                            reports.filter { it.priority == "HIGH" }

                        AdminFilter.FLOOR_1 ->
                            reports.filter { it.floor == "1" }

                        AdminFilter.IT ->
                            reports.filter { it.category == "IT" }

                        AdminFilter.WATER ->
                            reports.filter { it.category == "WATER" }
                    }
                    filtered
                }
                .collect { filteredReports ->
                    _state.value = AdminState.Success(filteredReports)
                }


        }
    }
}

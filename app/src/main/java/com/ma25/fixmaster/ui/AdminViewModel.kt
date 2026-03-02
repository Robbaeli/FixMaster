package com.ma25.fixmaster.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma25.fixmaster.model.IssueReport
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

    private fun priorityWeight(priority: String): Int {
        return when (priority) {
            "HIGH" -> 3
            "MEDIUM" -> 2
            else -> 1
        }
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
                        .sortedWith(
                            compareByDescending<IssueReport> { priorityWeight(it.priority)
                            }.thenByDescending {
                                it.timestamp?.toDate()?.time ?: 0L }
                        )
                }
                .collect { filteredReports ->
                    _state.value = AdminState.Success(filteredReports)
                }


        }
    }
}

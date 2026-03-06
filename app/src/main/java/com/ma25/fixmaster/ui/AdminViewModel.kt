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

    //Prioritet filter
    private fun priorityOrder(priority: String?): Int {
        return when (priority) {
            "HIGH" -> 3
            "MEDIUM"-> 2
            "LOW" -> 1
            else ->0
        }
    }


    fun loadReports() {
        viewModelScope.launch {

            try {

                repository.observeReports()
                    .combine(selectedFilter) { reports, filter ->
                        val filtered = when (filter) {
                            AdminFilter.ALL -> reports

                            AdminFilter.HIGH_PRIORITY ->
                                reports.filter { it.priority == "HIGH" }

                            AdminFilter.MEDIUM_PRIORITY ->
                                reports.filter { it.priority == "MEDIUM" }

                            AdminFilter.LOW_PRIORITY ->
                                reports.filter { it.priority == "LOW" }

                        }
                        filtered.sortedWith(compareByDescending<com.ma25.fixmaster.model.IssueReport>{
                            priorityOrder(it.priority)
                        }.thenByDescending {
                            it.timestamp.toDate().time
                        }
                        )

                    }
                    .collect { sortedReports ->
                        _state.value = AdminState.Success(sortedReports)
                    }

            } catch (_: Exception) {
                _state.value = AdminState.Error("Failed to load reports")
            }
        }

    }
}

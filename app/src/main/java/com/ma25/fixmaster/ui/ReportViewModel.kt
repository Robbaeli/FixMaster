package com.ma25.fixmaster.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.ma25.fixmaster.model.IssueReport
import com.ma25.fixmaster.model.Priority
import com.ma25.fixmaster.repository.ObjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {

    private val repo = ObjectRepository()

    private val _state = MutableStateFlow<ReportState>(ReportState.Idle)
    val state: StateFlow<ReportState> = _state

    fun submitReport(
        objectId: String,
        objectName: String,
        faultType: String,
        priority: Priority,
        createdBy: String,
        imageUri: Uri?,
        comment: String? //  behåll detta namn för att matcha call-site
    ) {
        viewModelScope.launch {
            _state.value = ReportState.Loading
            try {
                val report = IssueReport(
                    objectId = objectId,
                    objectName = objectName,
                    qrCode = objectId,
                    description = faultType,
                    status = "Ny",
                    imageUrl = null,
                    timestamp = Timestamp.now(),
                    completedTimestamp = null,
                    createdBy = createdBy,
                    priority = priority.name,
                    floor = "",
                    category = "",

                    // nya fält
                    userComment = comment,
                    adminComment = null
                )

                repo.addReport(report)

                _state.value = ReportState.Success
            } catch (e: Exception) {
                _state.value = ReportState.Error(e.message ?: "Kunde inte skicka rapport")
            }
        }
    }
}
package com.ma25.fixmaster.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.ma25.fixmaster.model.IssueReport
import com.ma25.fixmaster.repository.ObjectRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ReportViewModel : ViewModel() {

    private val repository = ObjectRepository()
    private val storage = FirebaseStorage.getInstance()

    private val _state = MutableStateFlow<ReportState>(ReportState.Idle)
    val state: StateFlow<ReportState> = _state

    fun submitReport(
        objectId: String,
        objectName: String,
        faultType: String,
        createdBy: String,
        imageUri: Uri?,
        comment: String?
    ) {
        _state.value = ReportState.Loading

        viewModelScope.launch {
            try {
                delay(1000)

                var downloadUrl: String? = null

                if (imageUri != null) {
                    val fileName = "reports/${UUID.randomUUID()}.jpg"
                    val ref = storage.reference.child(fileName)
                    ref.putFile(imageUri).await()
                    downloadUrl = ref.downloadUrl.await().toString()
                }

                val newReport = IssueReport(
                    objectId = objectId,
                    objectName = objectName,
                    qrCode = objectId,           // om objectId är QR-data i ert flöde
                    description = faultType,
                    status = "Ny",
                    imageUrl = downloadUrl,
                    createdBy = createdBy,
                    comment = comment
                )

                repository.addReport(newReport)

                _state.value = ReportState.Success
            } catch (e: Exception) {
                _state.value = ReportState.Error("Fel: ${e.message}")
            }
        }
    }
}
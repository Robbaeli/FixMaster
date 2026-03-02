package com.ma25.fixmaster.ui

import android.net.Uri // <--- Ny import
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage // <--- Ny import
import com.ma25.fixmaster.data.model.IssueReport
import com.ma25.fixmaster.model.Priority // <-- Ny import för priority
import com.ma25.fixmaster.repository.ObjectRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // <--- Ny import
import java.util.UUID // <--- Ny import

class ReportViewModel : ViewModel() {

    private val repository = ObjectRepository()

    // <--- ROBIN: Initierar Firebase Storage (US4)
    private val storage = FirebaseStorage.getInstance()

    private val _state = MutableStateFlow<ReportState>(ReportState.Idle)
    val state: StateFlow<ReportState> = _state

    // <--- ROBIN: Lade till "imageUri: Uri?" i slutet
    fun submitReport(
        objectId: String,
        objectName: String,
        faultType: String,
        createdBy: String,
        priority: Priority,
        imageUri: Uri?)
    {
        _state.value = ReportState.Loading

        viewModelScope.launch {
            try {
                // Simulera nätverksfördröjning
                delay(1000)

                // <--- ROBIN: NY LOGIK BÖRJAR (Laddar upp bilden om den finns)
                var downloadUrl: String? = null

                if (imageUri != null) {
                    val fileName = "reports/${UUID.randomUUID()}.jpg"
                    val ref = storage.reference.child(fileName)
                    ref.putFile(imageUri).await()
                    downloadUrl = ref.downloadUrl.await().toString()
                }
                // <--- ROBIN: NY LOGIK SLUTAR

                val newReport = IssueReport(
                    objectId = objectId,
                    objectName = objectName,
                    description = faultType,
                    status = "Ny",
                    imageUrl = downloadUrl, // <--- ROBIN: Skickar med länken hit (US4)
                    createdBy = createdBy,
                    priority = priority.name
                )

                // Här kan vi  byta till ObjectRepository om vi  vill köra Firebase tex vid att skapa manuellt objekt
                //Micke - La till ObjectRepository
                repository.addReport(newReport)

                _state.value = ReportState.Success
            } catch (e: Exception) {
                _state.value = ReportState.Error("Fel: ${e.message}")
            }
        }
    }
}
package com.ma25.fixmaster.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Nu bör denna bli lila/grön istället för röd
import com.ma25.fixmaster.repository.ObjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QrScannerViewModel : ViewModel() {

    // Skapa en instans av ditt repository
    private val repository = ObjectRepository()

    // Detta är appens "tillstånd". Vi börjar i Idle (vänteläge).
    private val _qrState = MutableStateFlow<QrState>(QrState.Idle)
    val qrState: StateFlow<QrState> = _qrState

    // Denna funktion anropas när kameran har läst en QR-kod
    fun onQrScanned(code: String) {
        // Om vi redan laddar, gör inget mer (förhindrar dubbla anrop)
        if (_qrState.value is QrState.Loading) return

        // Sätt status till Loading
        _qrState.value = QrState.Loading

        // Starta en rutine för att hämta data från Firebase i bakgrunden
        viewModelScope.launch {
            val result = repository.getObjectByQr(code)

            if (result != null) {
                // Succé! Vi hittade objektet i databasen
                _qrState.value = QrState.Success(result)
            } else {
                // Fel! QR-koden fanns inte i systemet
                _qrState.value = QrState.Error("Objektet hittades inte i databasen.")
            }
        }
    }
}
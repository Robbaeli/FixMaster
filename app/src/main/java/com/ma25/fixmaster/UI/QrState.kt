package com.ma25.fixmaster.UI

// TA BORT DENNA: import com.ma25.fixmaster.model.ReportObject
// BEHÅLL DENNA:
import com.ma25.fixmaster.data.model.ReportObject

sealed class QrState {
    object Idle : QrState()
    object Loading : QrState()
    data class Success(val reportObject: ReportObject) : QrState()
    data class Error(val message: String) : QrState()
}
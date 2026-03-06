package com.ma25.fixmaster.ui.qr

import com.ma25.fixmaster.model.ReportObject

sealed class QrState {
    object Idle : QrState()
    object Loading : QrState()
    data class Success(val reportObject: ReportObject) : QrState()
    data class Error(val message: String) : QrState()
}
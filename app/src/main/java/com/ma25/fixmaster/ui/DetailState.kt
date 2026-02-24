package com.ma25.fixmaster.ui

sealed class DetailState {
    object Idle : DetailState()
    object Loading : DetailState()
    object Success : DetailState()
    data class Error(val message: String) : DetailState()
}
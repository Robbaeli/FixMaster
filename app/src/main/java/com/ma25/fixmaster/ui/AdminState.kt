package com.ma25.fixmaster.ui

import com.ma25.fixmaster.model.Report

sealed class AdminState {

    object Loading : AdminState()
    data class Success(val reports: List<Report>) : AdminState()
    data class Error(val message: String) : AdminState()


}
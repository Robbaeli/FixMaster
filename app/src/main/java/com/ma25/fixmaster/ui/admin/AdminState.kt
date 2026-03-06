package com.ma25.fixmaster.ui.admin

import com.ma25.fixmaster.model.IssueReport

sealed class AdminState {

    object Loading : AdminState()
    data class Success(val reports: List<IssueReport>) : AdminState()
    data class Error(val message: String) : AdminState()


}
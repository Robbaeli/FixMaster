package com.ma25.fixmaster.data.model

import com.google.firebase.Timestamp

data class IssueReport(
    val id: String="",
    val objectId: String = "",
    val objectName: String = "",
    val description: String = "",
    val status: String = "Inskickad",
    val timestamp: Timestamp = Timestamp.now(),
    val completedTimestamp: Timestamp? = null,
    val createdBy: String=""
)
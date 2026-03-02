package com.ma25.fixmaster.model

import com.google.firebase.Timestamp
//import com.ma25.fixmaster.model.Priority väntar på att fråga Robin

data class IssueReport(
    val id: String = "",

    val objectId: String = "",
    val objectName: String = "",


    val description: String = "",
    val comment: String? = null,
    val imageUrl: String? = null,

    val status: String = "Ny",
    val timestamp: Timestamp = Timestamp.now(),
    val completedTimestamp: Timestamp? = null,

    val createdBy: String = ""
)
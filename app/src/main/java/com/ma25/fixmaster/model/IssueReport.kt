package com.ma25.fixmaster.model

import com.google.firebase.Timestamp

data class IssueReport(
    val id: String = "",

    val objectId: String = "",
    val objectName: String = "",

    val qrCode: String = "",

    val description: String = "",
    val comment: String? = null,
    val imageUrl: String? = null,

    val status: String = "Ny",
    val timestamp: Timestamp = Timestamp.now(),
    val completedTimestamp: Timestamp? = null,

    val createdBy: String = ""
)
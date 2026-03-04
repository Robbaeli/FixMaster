package com.ma25.fixmaster.model

import com.google.firebase.Timestamp

data class IssueReport(
    val id: String = "",

    val objectId: String = "",
    val objectName: String = "",
    val qrCode: String = "",
    val description: String = "",
    val status: String = "",
    val imageUrl: String? = null,
    val timestamp: Timestamp = Timestamp.now(),
    val completedTimestamp: Timestamp? = null,
    val createdBy: String = "",
    val priority: String = "",
    val floor: String = "",
    val category: String = "",

    // Två separata kommentarer
    val userComment: String? = null,
    val adminComment: String? = null
)
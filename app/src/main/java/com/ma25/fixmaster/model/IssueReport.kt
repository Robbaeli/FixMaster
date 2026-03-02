package com.ma25.fixmaster.data.model

import com.google.firebase.Timestamp

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
    val createdBy: String="",
    val priority: String="",
    val floor: String="",
    val category: String="",
    val qrCode: String = "",


)
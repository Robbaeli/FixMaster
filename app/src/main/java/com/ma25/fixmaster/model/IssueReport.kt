package com.ma25.fixmaster.data.model

import com.google.firebase.Timestamp

data class IssueReport(
    val id: String = "",
    val objectId: String = "",
    val objectName: String = "",
    val description: String = "",
    val status: String = "Inskickad",
    val imageUrl: String? = null, // <--- ROBIN: Nytt fält för bildlänk (US4)
    val timestamp: Timestamp = Timestamp.now(),
    val completedTimestamp: Timestamp? = null,
    val createdBy: String=""
    val status: String = "Ny",

    val qrCode: String = "",

    val timestamp: Timestamp? = null,

    // ✅ IMPORTANT: this is required for user filtering + Firestore rules
    val createdBy: String = ""
)
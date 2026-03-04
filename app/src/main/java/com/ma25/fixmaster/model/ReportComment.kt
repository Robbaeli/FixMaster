package com.ma25.fixmaster.model

import com.google.firebase.Timestamp

data class ReportComment(
    val id: String = "",
    val text: String = "",
    val authorUid: String = "",
    val authorRole: String = "", // "user" / "admin"
    val createdAt: Timestamp = Timestamp.now()
)
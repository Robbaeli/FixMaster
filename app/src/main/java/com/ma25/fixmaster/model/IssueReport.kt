package com.ma25.fixmaster.data.model

import com.google.firebase.Timestamp
//import com.ma25.fixmaster.model.Priority väntar på att fråga Robin

data class IssueReport(
    val id: String = "",
    val objectId: String = "",
    val objectName: String = "",
    val description: String = "",
    val status: String = "",
    val imageUrl: String? = null, // <--- ROBIN: Nytt fält för bildlänk (US4)
    val timestamp: Timestamp? = null,
    val completedTimestamp: Timestamp? = null,
    val createdBy: String="",
    val priority: String="",
    //val priority: Priority = Priority.LOW, //Inte hårdkodad String längre <- Väntar på Robin
    val floor: String="",
    val category: String="",


    val qrCode: String = "",
)
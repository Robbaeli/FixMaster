package com.ma25.fixmaster.model

data class Report(
    val id: String = "",
    val objectId: String = "",
    val objectName: String = "",
    val location: String = "Ej angiven",
    val faultType: String = "",
    val status: String = "Ny",
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String? = null
)
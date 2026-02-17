package com.ma25.fixmaster.model

data class Report (

    val id: String,
    val objectName: String,
    val location: String,
    val faultType: String,
    val status: String = "Ny",
    val timestamp: Long = System.currentTimeMillis()
)
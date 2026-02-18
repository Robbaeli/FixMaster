package com.ma25.fixmaster.model

data class Report(
    val id: String = "",
    val objectId: String,
    val objectName: String,
    val faultType: String,
    val status: String = "Ny",
    val createdAt: Long = System.currentTimeMillis(),
    val userId: String? = null // Sprint 1: kan vara null/dummy
)

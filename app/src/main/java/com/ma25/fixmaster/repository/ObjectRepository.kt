package com.ma25.fixmaster.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.ma25.fixmaster.model.IssueReport
import com.ma25.fixmaster.model.ReportObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ObjectRepository : ReportRepository {

    private val db = FirebaseFirestore.getInstance()

    // ========================
    // OBJECTS: get by QR
    // ========================
    suspend fun getObjectByQr(code: String): ReportObject? {
        return try {
            val snapshot = db.collection("objects")
                .whereEqualTo("qrCode", code)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                snapshot.documents[0].toObject(ReportObject::class.java)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // ========================
    // CREATE REPORT (User) - OFFLINE FRIENDLY ✅
    // ========================
    override suspend fun addReport(report: IssueReport) {
        val reportToSave = report.copy(
            // تأكد يوجد timestamp محلي حتى Offline
            timestamp = report.timestamp ?: Timestamp.now()
        )

        // IMPORTANT: لا await هنا، لكي لا يعلق Offline
        db.collection("reports").add(reportToSave)
    }

    // ========================
    // ADMIN - realtime list (status != Klar)
    // ========================
    override fun observeReports(): Flow<List<IssueReport>> = callbackFlow {
        val listener = db.collection("reports")
            .whereNotEqualTo("status", "Klar")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val reports = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(IssueReport::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(reports)
            }

        awaitClose { listener.remove() }
    }

    // ========================
    // USER - realtime list (createdBy == uid)
    // ========================
    override fun observeMyReports(uid: String): Flow<List<IssueReport>> = callbackFlow {
        val listener = db.collection("reports")
            .whereEqualTo("createdBy", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val myReports = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(IssueReport::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(myReports)
            }

        awaitClose { listener.remove() }
    }

    // ========================
    // Report by ID
    // ========================
    suspend fun getReportById(reportId: String): IssueReport? {
        return try {
            val doc = db.collection("reports")
                .document(reportId)
                .get()
                .await()

            doc.toObject(IssueReport::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    // ========================
    // ADMIN - update status
    // ========================
    override suspend fun updateReportStatus(reportId: String, newStatus: String) {
        val updateData = mutableMapOf<String, Any>(
            "status" to newStatus
        )

        if (newStatus == "Klar") {
            updateData["completedTimestamp"] = FieldValue.serverTimestamp()
        }

        db.collection("reports")
            .document(reportId)
            .update(updateData)
            .await()
    }
}
package com.ma25.fixmaster.repository

import com.google.firebase.firestore.FieldValue
import com.ma25.fixmaster.data.model.ReportObject
import com.ma25.fixmaster.data.model.IssueReport // Importera den nya modellen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ObjectRepository : ReportRepository {
    private val db = FirebaseFirestore.getInstance()

    // Hämta objekt baserat på QR-kod
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

    //  FUNKTION: Skicka in felrapport till Firebase
    suspend fun sendIssueReport(report: IssueReport): Boolean {
        return try {
            db.collection("reports") // Skapar/använder kollektionen "reports"
                .add(report)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    //Saknades addReport funktion

    override suspend fun addReport(report: IssueReport) {
        db.collection("reports")
            .add(report)
            .await()
    }

    //========================
    //ADMIN - REALTIME LISTA
    //========================
    override fun observeReports(): Flow<List<IssueReport>> = callbackFlow{

        val listener =db.collection("reports")
            .whereNotEqualTo("status", "Klar")
            .addSnapshotListener{ snapshot, error ->

                if (error != null) {
                close(error)
                return@addSnapshotListener
            }

                val reports = snapshot?.documents?.mapNotNull {doc ->
                    doc.toObject(IssueReport::class.java)
                        ?.copy(id=doc.id)
                } ?: emptyList()

                trySend(reports)

            }
        awaitClose {listener.remove()}


    }
    //=====================
    //ADMIN UPDATE STATUS
    //=====================
    override suspend fun updateReportStatus(
        reportId: String,
        newStatus: String
    ){

        val updateData = mutableMapOf<String, Any>(
            "status" to newStatus,
        )

        if (newStatus == "Klar") {
            updateData["completedTimestamp"] =
                FieldValue.serverTimestamp()
        }

        db.collection("reports")
            .document(reportId)
            .update(updateData)
            .await()
    }
}

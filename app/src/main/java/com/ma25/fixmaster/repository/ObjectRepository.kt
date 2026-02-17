package com.ma25.fixmaster.repository

import com.ma25.fixmaster.data.model.ReportObject
import com.ma25.fixmaster.data.model.IssueReport // Importera den nya modellen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ObjectRepository {
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
}
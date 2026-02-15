package com.ma25.fixmaster.repository

// BEHÅLL DENNA:
import com.ma25.fixmaster.data.model.ReportObject
import com.google.firebase.firestore.FirebaseFirestore
// TA BORT DENNA RAD (den orsakar felet):
// import com.ma25.fixmaster.model.ReportObject
import kotlinx.coroutines.tasks.await

class ObjectRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getObjectByQr(code: String): ReportObject? {
        return try {
            val snapshot = db.collection("objects")
                .whereEqualTo("qrCode", code)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                // Här används nu ReportObject från rätt paket (data.model)
                snapshot.documents[0].toObject(ReportObject::class.java)
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
package com.ma25.fixmaster.UI

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.ma25.fixmaster.R
import android.widget.TextView

class ReportActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var tvObjectName: TextView
    private lateinit var etDescription: TextInputEditText
    private lateinit var btnSendReport: Button

    private var currentObjectName: String = "Okänt objekt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        db = FirebaseFirestore.getInstance()

        // Koppla UI-komponenter med ID:n från din activity_report.xml
        tvObjectName = findViewById(R.id.tv_object_name)
        etDescription = findViewById(R.id.et_description)
        btnSendReport = findViewById(R.id.btn_send_report)

        val qrData = intent.getStringExtra("QR_DATA")

        if (qrData != null) {
            fetchObjectDataByQuery(qrData)
        } else {
            tvObjectName.text = "Ingen kod skannad"
        }

        btnSendReport.setOnClickListener {
            if (qrData != null) {
                sendReportToFirebase(qrData)
            }
        }
    }

    private fun fetchObjectDataByQuery(qrString: String) {
        db.collection("objects")
            .whereEqualTo("qrCode", qrString)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    currentObjectName = document.getString("name") ?: "Namnlöst objekt"
                    tvObjectName.text = currentObjectName
                } else {
                    tvObjectName.text = "Objektet saknas"
                    // Logga för debug i Logcat
                    println("DEBUG: Letade efter qrCode: $qrString men hittade inget.")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Sökfel: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun sendReportToFirebase(qrString: String) {
        val description = etDescription.text.toString().trim()

        if (description.isEmpty()) {
            etDescription.error = "Beskriv felet först"
            return
        }

        val report = hashMapOf(
            "objectName" to currentObjectName,
            "qrCode" to qrString,
            "description" to description,
            "timestamp" to Timestamp.now(),
            "status" to "Ny"
        )

        db.collection("reports")
            .add(report)
            .addOnSuccessListener {
                Toast.makeText(this, "Rapporten har skickats!", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Kunde inte spara: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
package com.ma25.fixmaster

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ma25.fixmaster.ui.AdminDashboardActivity
import com.ma25.fixmaster.ui.ReportUser

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 1) Skapa felanmälan -> MainActivity
        findViewById<MaterialButton>(R.id.btnCreateReport).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // 2) Mina felanmälningar -> ReportUser
        findViewById<MaterialButton>(R.id.btnMyReports).setOnClickListener {
            startActivity(Intent(this, ReportUser::class.java))
        }

        // 3) Admin -> kontrollera role i Firestore
        findViewById<MaterialButton>(R.id.btnAdminView).setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "Du är inte inloggad.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role")
                    if (role == "admin") {
                        startActivity(Intent(this, AdminDashboardActivity::class.java))
                    } else {
                        Toast.makeText(
                            this,
                            "Åtkomst nekad: Endast för administratörer.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Kunde inte verifiera behörighet.", Toast.LENGTH_SHORT).show()
                }
        }

        // 4) Info-knapp för Admin
        findViewById<ImageButton>(R.id.btnAdminInfo).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Admin-vy")
                .setMessage("Här kan administratörer se och hantera alla inkomna felanmälningar.")
                .setPositiveButton("OK", null)
                .show()
        }

        // 5) Logga ut
        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            auth.signOut()
            val i = Intent(this, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }

        // 6) Tillbaka (tidigare Radera konto)
        findViewById<MaterialButton>(R.id.btnDeleteAccount).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }
    }
}
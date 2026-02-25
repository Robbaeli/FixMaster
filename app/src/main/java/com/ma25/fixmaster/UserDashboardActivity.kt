package com.ma25.fixmaster

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore // <-- Ny import
import com.ma25.fixmaster.ui.ReportUser
import com.ma25.fixmaster.ui.AdminDashboardActivity

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore // <-- Definiera databasen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance() // <-- Starta databasen

        // 1. Skapa felanmälan -> MainActivity
        findViewById<MaterialButton>(R.id.btnCreateReport).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // 2. Mina felanmälningar -> ReportUser
        findViewById<MaterialButton>(R.id.btnMyReports).setOnClickListener {
            startActivity(Intent(this, ReportUser::class.java))
        }

        // 3. Admin -> LÅSET ÄR HÄR
        findViewById<MaterialButton>(R.id.btnAdminView).setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Hämta användarens dokument från Firestore
                db.collection("users").document(currentUser.uid).get()
                    .addOnSuccessListener { document ->
                        val role = document.getString("role")

                        // Kolla om rollen är "admin"
                        if (role == "admin") {
                            // Släpp in!
                            startActivity(Intent(this, AdminDashboardActivity::class.java))
                        } else {
                            // Stoppa vanliga användare
                            Toast.makeText(this, "Åtkomst nekad: Endast för administratörer.", Toast.LENGTH_LONG).show()
                        }
                    }
                    .addOnFailureListener {
                        // Om något går fel med internet/uppkopplingen
                        Toast.makeText(this, "Kunde inte verifiera behörighet.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Du är inte inloggad.", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Info-knapp för Admin
        findViewById<ImageButton>(R.id.btnAdminInfo).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Admin-vy")
                .setMessage("Här kan administratörer se och hantera alla inkomna felanmälningar.")
                .setPositiveButton("OK", null)
                .show()
        }

        // 5. Logga ut
        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            auth.signOut()
            val i = Intent(this, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }

        // 6. Radera konto (bekräftelse Ja/Nej)
        findViewById<MaterialButton>(R.id.btnDeleteAccount).setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Radera konto?")
                .setMessage("Är du säker? Detta går inte att ångra.")
                .setNegativeButton("Nej", null)
                .setPositiveButton("Ja") { _, _ ->

                    val user = auth.currentUser
                    if (user == null) {
                        Toast.makeText(this, "Du är inte inloggad.", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    val uid = user.uid

                    // (اختياري) احذف user document من Firestore أولاً
                    db.collection("users").document(uid).delete()
                        .addOnCompleteListener {

                            // احذف حساب Firebase Auth
                            user.delete()
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Kontot raderades.", Toast.LENGTH_SHORT).show()

                                    val i = Intent(this, LoginActivity::class.java)
                                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(i)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        e.message ?: "Kunde inte radera konto. Logga in igen och försök.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                }
                .show()
        }
    }
}
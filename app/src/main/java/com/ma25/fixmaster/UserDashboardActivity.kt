package com.ma25.fixmaster

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.ma25.fixmaster.ui.AdminDashboardActivity
import com.ma25.fixmaster.ReportUser

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        auth = FirebaseAuth.getInstance()

        // 1. Skapa felanmälan -> MainActivity
        findViewById<MaterialButton>(R.id.btnCreateReport).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // 2. Mina felanmälningar -> (Placeholder tills funktionen är byggd)
        findViewById<MaterialButton>(R.id.btnMyReports).setOnClickListener {
            startActivity(Intent(this, ReportUser::class.java))
        }

        // 3. Admin -> AdminDashboardActivity (Där listan finns)
        findViewById<MaterialButton>(R.id.btnAdminView).setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
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
    }
}
package com.ma25.fixmaster.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.ma25.fixmaster.R

class ResetPasswordActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmailReset)
        val btn = findViewById<MaterialButton>(R.id.btnSendReset)

        btn.setOnClickListener {
            val email = etEmail.text?.toString()?.trim().orEmpty()
            if (email.isBlank()) {
                Toast.makeText(this, "Skriv din email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(this, "Link skickad ✅", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message ?: "Failed", Toast.LENGTH_LONG).show()
                }
        }
    }
}
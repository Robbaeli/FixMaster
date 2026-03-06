package com.ma25.fixmaster.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ma25.fixmaster.R

class CreateAccountActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmailCreate)
        val etPass = findViewById<TextInputEditText>(R.id.etPassCreate)
        val btn = findViewById<MaterialButton>(R.id.btnCreate)

        btn.setOnClickListener {
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val pass = etPass.text?.toString()?.trim().orEmpty()

            if (email.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Fyll i email och lösenord", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pass.length < 6) {
                Toast.makeText(this, "Password måste vara minst 6 tecken", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener { res ->
                    val uid = res.user?.uid ?: return@addOnSuccessListener

                    // Default role = user
                    db.collection("users").document(uid)
                        .set(mapOf("role" to "user"))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Konto skapat ✅", Toast.LENGTH_SHORT).show()
                            finish() // يرجع لصفحة login
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, e.message ?: "Failed to save role", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message ?: "Create account failed", Toast.LENGTH_LONG).show()
                }
        }
    }
}
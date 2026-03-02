package com.ma25.fixmaster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ma25.fixmaster.ui.AdminDashboardActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var progress: View
    private lateinit var tvForgotPassword: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progress = findViewById(R.id.progress)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        // ✅ Backend-only: vi tar bort CreateAccount i appen (ingen click listener här)
        // Om din XML fortfarande har tvCreateAccount, kan du ta bort den där också.

        btnLogin.setOnClickListener { login() }

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        // ✅ Auto-login: hämta role och navigera rätt
        val current = auth.currentUser
        if (current != null) {
            setLoading(true)
            fetchRoleAndNavigate(current.uid)
        }
    }

    private fun login() {
        val email = etEmail.text?.toString()?.trim().orEmpty()
        val password = etPassword.text?.toString()?.trim().orEmpty()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Fyll i email och lösenord", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid == null) {
                    setLoading(false)
                    Toast.makeText(this, "Login failed (no uid)", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // ✅ Här: hämta role och navigera (admin/user)
                fetchRoleAndNavigate(uid)
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, e.message ?: "Login failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchRoleAndNavigate(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role")

                if (role.isNullOrBlank()) {
                    setLoading(false)
                    Toast.makeText(this, "No role found for this user", Toast.LENGTH_LONG).show()
                    auth.signOut()
                    return@addOnSuccessListener
                }

                when (role) {
                    "admin" -> goTo(UserDashboardActivity::class.java)
                    "user" -> goTo(UserDashboardActivity::class.java)
                    else -> {
                        setLoading(false)
                        Toast.makeText(this, "Unknown role: $role", Toast.LENGTH_LONG).show()
                        auth.signOut()
                    }
                }
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, e.message ?: "Failed to load role", Toast.LENGTH_LONG).show()
                auth.signOut()
            }
    }

    private fun goTo(clazz: Class<*>) {
        setLoading(false)
        val i = Intent(this, clazz)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }

    private fun setLoading(loading: Boolean) {
        progress.visibility = if (loading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !loading
        etEmail.isEnabled = !loading
        etPassword.isEnabled = !loading
    }
}
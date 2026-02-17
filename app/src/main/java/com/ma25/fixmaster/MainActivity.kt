package com.ma25.fixmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Button //TEST
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ma25.fixmaster.ui.AdminDashboardActivity //TEST
// BEHÅLL DENNA (om mappen heter UI):
import com.ma25.fixmaster.ui.QrScannerActivity

// TA BORT DENNA (den orsakar felet):
// import com.ma25.fixmaster.ui.scanner.QrScannerActivity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val fabScan = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_scan_qr)
        val btnAdmin = findViewById<Button>(R.id.test2) //test


        fabScan.setOnClickListener {
            val intent = Intent(this, QrScannerActivity::class.java)
            startActivity(intent)
        }

        btnAdmin.setOnClickListener { //TEST
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent) //TEST
        }
    }
}
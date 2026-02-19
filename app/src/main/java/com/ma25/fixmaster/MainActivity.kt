package com.ma25.fixmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ma25.fixmaster.ui.AdminDashboardActivity
import com.ma25.fixmaster.ui.QrScannerActivity
import com.ma25.fixmaster.ui.ReportActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 1. QR-SCANNEN (Den runda knappen)
        val fabScan = findViewById<FloatingActionButton>(R.id.fab_scan_qr)
        fabScan.setOnClickListener {
            startActivity(Intent(this, QrScannerActivity::class.java))
        }

        // 2. MANUELL RAPPORT (Knappen "Finns ingen QR kod?")
        val btnManual = findViewById<Button>(R.id.test2)
        btnManual.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java).apply {
                putExtra("QR_DATA", "MANUAL_ENTRY")
                putExtra("OBJECT_NAME", "Manuell inmatning")
            }
            startActivity(intent)
        }

        // 3. ADMIN VY (Den nya knappen vi lägger till)
        val btnAdmin = findViewById<Button>(R.id.btn_admin_view)
        btnAdmin.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
        }
    }
}
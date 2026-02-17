package com.ma25.fixmaster

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ma25.fixmaster.R // Denna rad fixar 'layout' och 'fab_scan_qr'
import com.ma25.fixmaster.UI.QrScannerActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val fabScan = findViewById<FloatingActionButton>(R.id.fab_scan_qr)

        fabScan.setOnClickListener {
            val intent = Intent(this, QrScannerActivity::class.java)
            startActivity(intent)
        }
    }
}
package com.ma25.fixmaster

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
// BEHÅLL DENNA (om mappen heter UI):
import com.ma25.fixmaster.UI.QrScannerActivity

// TA BORT DENNA (den orsakar felet):
// import com.ma25.fixmaster.ui.scanner.QrScannerActivity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val fabScan = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_scan_qr)

        fabScan.setOnClickListener {
            val intent = Intent(this, QrScannerActivity::class.java)
            startActivity(intent)
        }
    }
}
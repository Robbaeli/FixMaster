package com.ma25.fixmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ma25.fixmaster.ui.QrScannerActivity
import com.ma25.fixmaster.ui.ReportActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // QR-scan knapp (behåll)
        val fabScan = findViewById<FloatingActionButton>(R.id.fab_scan_qr)

        fabScan.setOnClickListener {
            val intent = Intent(this, QrScannerActivity::class.java)
            startActivity(intent)
        }

        // TEST-knapp för Sprint 1 (öppnar Report direkt)
        val testBtn = findViewById<Button>(R.id.test2)

        testBtn.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java).apply {
                putExtra(ReportActivity.EXTRA_OBJECT_ID, "coffee_machine_3")
                putExtra(ReportActivity.EXTRA_OBJECT_NAME, "Kaffemaskin 3")
            }
            startActivity(intent)
        }
    }
}

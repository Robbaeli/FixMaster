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

        val fabScan = findViewById<FloatingActionButton>(R.id.fab_scan_qr)
        fabScan.setOnClickListener {
            val intent = Intent(this, QrScannerActivity::class.java)
            startActivity(intent)
        }

        val testBtn = findViewById<Button>(R.id.test2)
        testBtn.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java).apply {
                putExtra(ReportActivity.EXTRA_OBJECT_ID, "test_id_123")
                putExtra(ReportActivity.EXTRA_OBJECT_NAME, "Test-objekt")
            }
            startActivity(intent)
        }
    }
}
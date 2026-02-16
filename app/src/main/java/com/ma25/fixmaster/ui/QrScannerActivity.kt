package com.ma25.fixmaster.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ma25.fixmaster.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QrScannerActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 101
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner)

        // Initiera executor för kameran
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Kontrollera om vi har tillstånd
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            // Fråga om tillstånd
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Används för att binda kamerans livscykel till aktiviteten
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Konfigurera Preview (kamerabilden)
            // Inuti startCamera-funktionen i QrScannerActivity.kt
            val preview = Preview.Builder()
                .build()
                .also {
                    // Vi letar upp vyn manuellt via dess ID från XML
                    val view = findViewById<androidx.camera.view.PreviewView>(R.id.previewView)
                    it.setSurfaceProvider(view.surfaceProvider)
                }

            // Välj den bakre kameran som standard
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Koppla ifrån allt innan vi startar på nytt
                cameraProvider.unbindAll()

                // Bind kameran till vår livscykel
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)

            } catch(exc: Exception) {
                Toast.makeText(this, "Kunde inte starta kamera: ${exc.message}", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(this, "Kameratillstånd krävs för att skanna", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stäng ner executor när aktiviteten förstörs
        cameraExecutor.shutdown()
    }
}
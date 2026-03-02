package com.ma25.fixmaster.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.ma25.fixmaster.R
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.ContextCompat
import com.ma25.fixmaster.model.Priority


class ReportActivity : AppCompatActivity() {

    private val viewModel: ReportViewModel by viewModels()

    private lateinit var tvTitle: TextView
    private lateinit var rgFaultType: RadioGroup
    private lateinit var rgPriority: RadioGroup // <--- NYTT: RadioGroup för prioritering
    private lateinit var btnSubmit: Button
    private lateinit var progress: ProgressBar

    private lateinit var objectId: String
    private lateinit var objectName: String

    //bifoga bild
    private lateinit var ivPreview: ImageView
    private lateinit var btnAttachImage: MaterialButton
    private var latestImageUri: Uri? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            // Om användaren tog en bild, visa den i  ImageView och gör den synlig!
            latestImageUri?.let { uri ->
                ivPreview.setImageURI(uri)
                ivPreview.isVisible = true
            }
        } else {
            Toast.makeText(this, "Ingen bild togs", Toast.LENGTH_SHORT).show()
        }
    }

    // 2. Launcher: Frågar användaren om lov att använda kameran första gången
    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            startCameraWrapper() // Tillåtelse gavs, starta kameran!
        } else {
            Toast.makeText(this, "Kameran krävs för att fota felet.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        readIntentExtras()
        bindViews()
        renderHeader()
        setupListeners()
        observeState()
    }

    private fun readIntentExtras() {
        objectId = intent.getStringExtra("QR_DATA") ?: intent.getStringExtra(EXTRA_OBJECT_ID) ?: "unknown"
        objectName = intent.getStringExtra("OBJECT_NAME") ?: intent.getStringExtra(EXTRA_OBJECT_NAME) ?: "Okänt objekt"
    }

    private fun bindViews() {
        tvTitle = findViewById(R.id.tvObjectTitle)
        rgFaultType = findViewById(R.id.rgFaultType)
        btnSubmit = findViewById(R.id.btnSubmit)
        progress = findViewById(R.id.progress)
        ivPreview = findViewById(R.id.ivPreview)
        btnAttachImage = findViewById(R.id.btnAttachImage)
        rgPriority = findViewById(R.id.rgPriority) // <--- NYTT: RadioGroup för prioritering
    }

    private fun renderHeader() {
        tvTitle.text = objectName
    }

    private fun setupListeners() {
        // 1. Klick för att ta bild
        btnAttachImage.setOnClickListener {
            checkCameraPermissionAndStart()
        } // <--- DENNA KLAMMER SAKNADES!

        // 2. Klick för att skicka rapport
        btnSubmit.setOnClickListener {
            val selectedId = rgFaultType.checkedRadioButtonId
            //Prioritet som Micke har lagt till
            val selectedPriorityId = rgPriority.checkedRadioButtonId

            val priority = when (selectedPriorityId) {
                R.id.rbHigh -> Priority.HIGH
                R.id.rbMedium -> Priority.MEDIUM
                R.id.rbLow -> Priority.LOW
                else -> Priority.LOW
            }



            if (selectedId == -1) {
                Toast.makeText(this, "Välj typ av fel", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val faultType = findViewById<RadioButton>(selectedId).text.toString()
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                Toast.makeText(this, "Du är inte inloggad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Skicka med allt, INKLUSIVE bilden!
            viewModel.submitReport(
                objectId = objectId,
                objectName = objectName,
                faultType = faultType,
                priority = priority, //Micke la till priority
                createdBy = uid,
                imageUri = latestImageUri // <--- NYTT: Skickar med bilden till ViewModel
            )
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    progress.isVisible = state is ReportState.Loading
                    btnSubmit.isEnabled = state !is ReportState.Loading

                    when (state) {
                        is ReportState.Success -> {
                            // Starta bekräftelsevyn
                            val intent = Intent(this@ReportActivity, ReportSuccessActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        is ReportState.Error -> {
                            Toast.makeText(this@ReportActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
    // Funktion som triggas när man klickar på "TA ETT FOTO"
    private fun checkCameraPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // Vi har redan tillåtelse, kör igång!
            startCameraWrapper()
        } else {
            // Vi måste fråga om tillåtelse först
            requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    // Funktion som skapar filen och öppnar kameran säkert
    private fun startCameraWrapper() {
        // Skapar en temporär fil i mappen vi angav i file_paths.xml
        val photoFile = java.io.File(externalCacheDirs.first(), "temp_photo_${System.currentTimeMillis()}.jpg")

        // 1. Skapar en säker URI och sparar den i en lokal, icke-null variabel (uri)
        val uri = androidx.core.content.FileProvider.getUriForFile(
            this,
            "com.ma25.fixmaster.fileprovider", // MÅSTE matcha namnet i din AndroidManifest!
            photoFile
        )

        // 2. Sparar den för senare bruk (när bilden ska visas och skickas)
        latestImageUri = uri

        // 3. Nu skickar vi den garanterat säkra variabeln 'uri' till kameran. Inget gnäll från Kotlin!
        takePictureLauncher.launch(uri)
    }

    companion object {
        const val EXTRA_OBJECT_ID = "extra_object_id"
        const val EXTRA_OBJECT_NAME = "extra_object_name"
    }
}
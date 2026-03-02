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
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.ma25.fixmaster.R
import kotlinx.coroutines.launch
import java.io.File

class ReportActivity : AppCompatActivity() {

    private val viewModel: ReportViewModel by viewModels()

    private lateinit var tvTitle: TextView
    private lateinit var rgFaultType: RadioGroup
    private lateinit var rgPriority: RadioGroup // <--- NYTT: RadioGroup för prioritering
    private lateinit var btnSubmit: Button
    private lateinit var progress: ProgressBar

    private lateinit var objectId: String
    private lateinit var objectName: String

    private lateinit var ivPreview: ImageView
    private lateinit var btnAttachImage: MaterialButton
    private var latestImageUri: Uri? = null

    private lateinit var etComment: TextInputEditText

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                latestImageUri?.let { uri ->
                    ivPreview.setImageURI(uri)
                    ivPreview.isVisible = true
                }
            } else {
                Toast.makeText(this, "Ingen bild togs", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) startCameraWrapper()
            else Toast.makeText(this, "Kameran krävs för att fota felet.", Toast.LENGTH_LONG).show()
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
        objectId = intent.getStringExtra("QR_DATA")
            ?: intent.getStringExtra(EXTRA_OBJECT_ID)
                    ?: "unknown"

        objectName = intent.getStringExtra("OBJECT_NAME")
            ?: intent.getStringExtra(EXTRA_OBJECT_NAME)
                    ?: "Okänt objekt"
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
        btnAttachImage.setOnClickListener { checkCameraPermissionAndStart() }

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

            val comment = etComment.text?.toString()
                .orEmpty()
                .trim()
                .takeIf { it.isNotBlank() } // null om tom

            viewModel.submitReport(
                objectId = objectId,
                objectName = objectName,
                faultType = faultType,
                priority = priority, //Micke la till priority
                createdBy = uid,
                imageUri = latestImageUri,
                comment = comment
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
                            startActivity(Intent(this@ReportActivity, ReportSuccessActivity::class.java))
                            finish()
                        }
                        is ReportState.Error -> {
                            Toast.makeText(this@ReportActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun checkCameraPermissionAndStart() {
        val granted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (granted) startCameraWrapper()
        else requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    private fun startCameraWrapper() {
        val photoFile = File(externalCacheDirs.first(), "temp_photo_${System.currentTimeMillis()}.jpg")

        val uri = androidx.core.content.FileProvider.getUriForFile(
            this,
            "com.ma25.fixmaster.fileprovider",
            photoFile
        )

        latestImageUri = uri
        takePictureLauncher.launch(uri)
    }

    companion object {
        const val EXTRA_OBJECT_ID = "extra_object_id"
        const val EXTRA_OBJECT_NAME = "extra_object_name"
    }
}
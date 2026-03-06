package com.ma25.fixmaster.ui.report

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.ma25.fixmaster.R
import com.ma25.fixmaster.model.Priority
import com.ma25.fixmaster.ui.report.state.ReportState
import kotlinx.coroutines.launch
import java.io.File

class ReportActivity : AppCompatActivity() {

    private val viewModel: ReportViewModel by viewModels()

    private lateinit var tvTitle: TextView
    private lateinit var rgFaultType: RadioGroup
    private lateinit var rgPriority: RadioGroup
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
        rgPriority = findViewById(R.id.rgPriority)
        btnSubmit = findViewById(R.id.btnSubmit)
        progress = findViewById(R.id.progress)
        ivPreview = findViewById(R.id.ivPreview)
        btnAttachImage = findViewById(R.id.btnAttachImage)
        etComment = findViewById(R.id.etComment)
    }

    private fun renderHeader() {
        tvTitle.text = objectName
    }

    private fun setupListeners() {
        btnAttachImage.setOnClickListener { checkCameraPermissionAndStart() }

        btnSubmit.setOnClickListener {
            val selectedFaultId = rgFaultType.checkedRadioButtonId
            val selectedPriorityId = rgPriority.checkedRadioButtonId

            if (selectedFaultId == -1) {
                Toast.makeText(this, "Välj typ av fel", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val priority = when (selectedPriorityId) {
                R.id.rbHigh -> Priority.HIGH
                R.id.rbMedium -> Priority.MEDIUM
                R.id.rbLow -> Priority.LOW
                else -> Priority.LOW
            }

            val faultType = findViewById<RadioButton>(selectedFaultId).text.toString()

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                Toast.makeText(this, "Du är inte inloggad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userComment = etComment.text
                ?.toString()
                .orEmpty()
                .trim()
                .takeIf { it.isNotBlank() }

            val online = isOnline()
            if (!online) {
                Toast.makeText(
                    this,
                    "Du är offline. Ärendet sparas och synkas när du är online.",
                    Toast.LENGTH_LONG
                ).show()
            }

            // Om offline: skicka inte bild (Firebase Storage funkar inte offline)
            val imageToSend = if (online) latestImageUri else null

            viewModel.submitReport(
                objectId = objectId,
                objectName = objectName,
                faultType = faultType,
                createdBy = uid,
                priority = priority,
                imageUri = imageToSend,
                comment = userComment
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
                            startActivity(
                                Intent(this@ReportActivity, ReportSuccessActivity::class.java)
                            )
                            finish()
                        }

                        is ReportState.Error -> {
                            Toast.makeText(
                                this@ReportActivity,
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
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
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) startCameraWrapper()
        else requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun startCameraWrapper() {
        val photoFile = File(externalCacheDirs.first(), "temp_photo_${System.currentTimeMillis()}.jpg")

        val uri = FileProvider.getUriForFile(
            this,
            "com.ma25.fixmaster.fileprovider",
            photoFile
        )

        latestImageUri = uri
        takePictureLauncher.launch(uri)
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(ConnectivityManager::class.java)
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    companion object {
        const val EXTRA_OBJECT_ID = "extra_object_id"
        const val EXTRA_OBJECT_NAME = "extra_object_name"
    }
}
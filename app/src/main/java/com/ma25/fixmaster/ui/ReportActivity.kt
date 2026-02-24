package com.ma25.fixmaster.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ma25.fixmaster.R
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

class ReportActivity : AppCompatActivity() {

    private val viewModel: ReportViewModel by viewModels()

    private lateinit var tvTitle: TextView
    private lateinit var rgFaultType: RadioGroup
    private lateinit var btnSubmit: Button
    private lateinit var progress: ProgressBar

    private lateinit var objectId: String
    private lateinit var objectName: String

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
    }

    private fun renderHeader() {
        tvTitle.text = objectName
    }

    private fun setupListeners() {
        btnSubmit.setOnClickListener {
            val selectedId = rgFaultType.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Välj typ av fel", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val faultType = findViewById<RadioButton>(selectedId).text.toString()
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid ==null) {
                Toast.makeText(this, "Du är inte inloggad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            viewModel.submitReport(
                objectId = objectId,
                objectName = objectName,
                faultType = faultType,
                createdBy = uid
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

    companion object {
        const val EXTRA_OBJECT_ID = "extra_object_id"
        const val EXTRA_OBJECT_NAME = "extra_object_name"
    }
}
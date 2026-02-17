package com.ma25.fixmaster.ui

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
        objectId = intent.getStringExtra(EXTRA_OBJECT_ID) ?: "unknown"
        objectName = intent.getStringExtra(EXTRA_OBJECT_NAME)
            ?: getString(R.string.report_object_prefix, "Okänt")
    }

    private fun bindViews() {
        tvTitle = findViewById(R.id.tvObjectTitle)
        rgFaultType = findViewById(R.id.rgFaultType)
        btnSubmit = findViewById(R.id.btnSubmit)
        progress = findViewById(R.id.progress)
    }

    private fun renderHeader() {
        tvTitle.text = getString(R.string.report_object_prefix, objectName)
    }

    private fun setupListeners() {
        btnSubmit.setOnClickListener {
            // Blockera dubbelklick om vi redan är i Loading
            if (!btnSubmit.isEnabled) return@setOnClickListener

            val selectedId = rgFaultType.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, getString(R.string.report_choose_fault), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val faultType = findViewById<RadioButton>(selectedId).text.toString()

            viewModel.submitReport(
                objectId = objectId,
                objectName = objectName,
                faultType = faultType,
                userId = null // Sprint 1: dummy
            )
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    // ✅ ProgressBar kopplad till state
                    progress.isVisible = state is ReportState.Loading

                    when (state) {
                        is ReportState.Idle -> setSubmitEnabled(true)

                        is ReportState.Loading -> setSubmitEnabled(false)

                        is ReportState.Success -> {
                            // ✅ Förbättring: säkerställ UI innan vi stänger
                            progress.isVisible = false
                            setSubmitEnabled(true)

                            Toast.makeText(
                                this@ReportActivity,
                                getString(R.string.report_sent_thanks),
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }

                        is ReportState.Error -> {
                            progress.isVisible = false
                            setSubmitEnabled(true)

                            Toast.makeText(
                                this@ReportActivity,
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun setSubmitEnabled(enabled: Boolean) {
        btnSubmit.isEnabled = enabled
        btnSubmit.text = getString(if (enabled) R.string.report_send else R.string.report_sending)

        // Lås valen också under loading (polish)
        rgFaultType.isEnabled = enabled
        for (i in 0 until rgFaultType.childCount) {
            rgFaultType.getChildAt(i).isEnabled = enabled
        }
    }

    companion object {
        const val EXTRA_OBJECT_ID = "extra_object_id"
        const val EXTRA_OBJECT_NAME = "extra_object_name"
    }
}

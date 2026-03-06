package com.ma25.fixmaster.ui.report

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.ma25.fixmaster.R
import com.ma25.fixmaster.model.Priority
import com.ma25.fixmaster.repository.ObjectRepository
import kotlinx.coroutines.launch

class ReportDetailActivity : AppCompatActivity() {

    private lateinit var reportId: String
    private val repo = ObjectRepository()

    private lateinit var progressBar: ProgressBar
    private lateinit var btnSave: Button
    private lateinit var spinnerStatus: Spinner
    private lateinit var spinnerPriority: Spinner
    private lateinit var imageView: ImageView

    private lateinit var tvUserComment: TextView
    private lateinit var tvAdminComment: TextView
    private lateinit var etAdminComment: TextInputEditText

    private val priorities = Priority.entries.map { it.name }
    private val statuses = listOf("Ny", "Påbörjad", "Klar")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail)

        reportId = intent.getStringExtra("reportId").orEmpty()
        if (reportId.isBlank()) {
            Toast.makeText(this, "Report ID saknas", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        imageView = findViewById(R.id.ivReportImage)
        progressBar = findViewById(R.id.progressBar)
        btnSave = findViewById(R.id.btnSave)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        spinnerPriority = findViewById(R.id.spinnerPriority)

        tvUserComment = findViewById(R.id.tvUserComment)
        tvAdminComment = findViewById(R.id.tvAdminComment)
        etAdminComment = findViewById(R.id.etAdminComment)

        spinnerStatus.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            statuses
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinnerPriority.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            priorities
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        loadReport()

        btnSave.setOnClickListener {
            saveChanges()
        }
    }

    private fun loadReport() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            btnSave.isEnabled = false

            val report = repo.getReportById(reportId)

            if (report == null) {
                progressBar.visibility = View.GONE
                btnSave.isEnabled = true
                Toast.makeText(this@ReportDetailActivity, "Kunde inte ladda rapporten", Toast.LENGTH_LONG).show()
                return@launch
            }

            tvUserComment.text = report.userComment ?: "-"
            tvAdminComment.text = report.adminComment ?: "-"

            // Admin kan skriva sin egen kommentar (separat fält)
            etAdminComment.setText(report.adminComment.orEmpty())

            val statusPos = statuses.indexOf(report.status)
            if (statusPos >= 0) spinnerStatus.setSelection(statusPos)

            val prioPos = priorities.indexOf(report.priority)
            if (prioPos >= 0) spinnerPriority.setSelection(prioPos)

            if (!report.imageUrl.isNullOrBlank()) {
                imageView.visibility = View.VISIBLE
                Glide.with(this@ReportDetailActivity)
                    .load(report.imageUrl)
                    .into(imageView)
            } else {
                imageView.visibility = View.GONE
            }

            progressBar.visibility = View.GONE
            btnSave.isEnabled = true
        }
    }

    private fun saveChanges() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            btnSave.isEnabled = false

            val newStatus = spinnerStatus.selectedItem.toString()
            val newPriority = spinnerPriority.selectedItem.toString()

            val adminComment = etAdminComment.text?.toString()
                .orEmpty()
                .trim()
                .takeIf { it.isNotBlank() } // null om tom

            try {
                //  hanterar status + prio + adminComment
                repo.updateReport(
                    reportId = reportId,
                    newStatus = newStatus,
                    newPriority = newPriority,
                    adminComment = adminComment,


                )

                progressBar.visibility = View.GONE
                finish()
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                btnSave.isEnabled = true
                Toast.makeText(
                    this@ReportDetailActivity,
                    e.message ?: "Något gick fel vid sparning",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
package com.ma25.fixmaster.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.ma25.fixmaster.R
import com.ma25.fixmaster.model.Priority
import com.ma25.fixmaster.repository.ObjectRepository
import kotlinx.coroutines.launch

class ReportDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: ReportDetailViewModel
    private lateinit var reportId: String
    private lateinit var progressBar: ProgressBar
    private lateinit var btnSave: Button
    private lateinit var spinner: Spinner
    private lateinit var imageView: ImageView

    private lateinit var spinnerPriority: Spinner

    private val priorities = Priority.entries.map { it.name }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail)

        //Bild fång
        imageView = findViewById(R.id.ivReportImage)

        reportId = intent.getStringExtra("reportId") ?: ""

        //Init UI
        progressBar = findViewById(R.id.progressBar)
        btnSave = findViewById(R.id.btnSave)
        spinner = findViewById(R.id.spinnerStatus)

        spinnerPriority = findViewById(R.id.spinnerPriority)

        val priorityAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            priorities

        )

        priorityAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerPriority.adapter = priorityAdapter


        //Spinner värden
        val statuses = listOf("Ny", "Påbörjad", "Klar")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            statuses

        )
        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item

        )
        spinner.adapter = adapter

        val currentStatus = intent.getStringExtra("currentStatus")

        val position = statuses.indexOf(currentStatus)

        if (position >= 0) {
            spinner.setSelection(position)
        }

    }

    override fun onStart() {
        super.onStart()

        //Init ViewModel
        viewModel = ReportDetailViewModel(ObjectRepository())

        //ladda rapport med bild
        loadReport()

        //Spara knapp
        btnSave.setOnClickListener {
            val newStatus = spinner.selectedItem.toString()
            val newPriority = spinnerPriority.selectedItem.toString()
            viewModel.updateStatus(reportId, newStatus, newPriority)
        }
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is DetailState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        btnSave.isEnabled = false
                    }

                    is DetailState.Success -> {
                        progressBar.visibility = View.GONE
                        finish() //tillbaka till admin listan
                    }

                    is DetailState.Error -> {
                        progressBar.visibility = View.GONE
                        btnSave.isEnabled = true
                        Toast.makeText(
                            this@ReportDetailActivity,
                            state.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun loadReport() {
        lifecycleScope.launch {
            val report = ObjectRepository()
                .getReportById(reportId)

            report?.let { reportItem ->

                if (reportItem.imageUrl != null) {
                    imageView.visibility = View.VISIBLE

                    Glide.with(this@ReportDetailActivity)
                        .load(report.imageUrl)
                        .into(imageView)

                } else {
                    imageView.visibility = View.GONE
                }

                val priorityPosition = priorities.indexOf(reportItem.priority)
                if (priorityPosition >= 0) {
                    spinnerPriority.setSelection(priorityPosition)
                }
            }
        }
    }
}
package com.ma25.fixmaster.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ma25.fixmaster.R
import com.ma25.fixmaster.repository.ObjectRepository
import kotlinx.coroutines.launch

class ReportDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: ReportDetailViewModel
    private lateinit var reportId: String
    private lateinit var progressBar: ProgressBar
    private lateinit var btnSave: Button
    private lateinit var spinner: Spinner



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail)

        reportId = intent.getStringExtra("reportId") ?: ""

        //Init UI
        progressBar = findViewById(R.id.progressBar)
        btnSave = findViewById(R.id.btnSave)
        spinner = findViewById(R.id.spinnerStatus)

        //Spinner värden
        val statuses = listOf("Ny","Påbörjad","Klar")

        val adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            statuses
        )
        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
                spinner.adapter = adapter

    }

    override fun onStart() {
        super.onStart()

        //Init ViewModel
        viewModel = ReportDetailViewModel(ObjectRepository())

        //Spara knapp
        btnSave.setOnClickListener {
        val newStatus = spinner.selectedItem.toString()
        viewModel.updateStatus(reportId,newStatus)
    }
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                is DetailState.Loading ->{
                    progressBar.visibility = View.VISIBLE
                    btnSave.isEnabled = false
                }
                is DetailState.Success ->{
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
}
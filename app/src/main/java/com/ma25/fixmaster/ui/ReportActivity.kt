package com.ma25.fixmaster.ui

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ma25.fixmaster.R
import kotlinx.coroutines.launch

class ReportActivity : AppCompatActivity() {

    private val viewModel: ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val objectId = intent.getStringExtra(EXTRA_OBJECT_ID) ?: "unknown"
        val objectName = intent.getStringExtra(EXTRA_OBJECT_NAME) ?: "Okänt objekt"

        val tvTitle = findViewById<TextView>(R.id.tvObjectTitle)
        val rg = findViewById<RadioGroup>(R.id.rgFaultType)
        val btn = findViewById<Button>(R.id.btnSubmit)

        tvTitle.text = "Objekt: $objectName"

        btn.setOnClickListener {
            val selectedId = rg.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Välj en feltyp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedText = findViewById<RadioButton>(selectedId).text.toString()
            viewModel.submitReport(
                objectId = objectId,
                objectName = objectName,
                faultType = selectedText,
                userId = null // Sprint 1: dummy
            )
        }

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is ReportState.Success -> {
                        Toast.makeText(this@ReportActivity, "Tack! Ärendet är skickat.", Toast.LENGTH_SHORT).show()
                        finish() // tillbaka
                    }
                    is ReportState.Error -> Toast.makeText(this@ReportActivity, state.message, Toast.LENGTH_SHORT).show()
                    else -> Unit
                }
            }
        }
    }

    companion object {
        const val EXTRA_OBJECT_ID = "extra_object_id"
        const val EXTRA_OBJECT_NAME = "extra_object_name"
    }
}

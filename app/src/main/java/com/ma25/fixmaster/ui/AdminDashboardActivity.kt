package com.ma25.fixmaster.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ma25.fixmaster.R
import kotlinx.coroutines.launch

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var viewModel: AdminViewModel
    private lateinit var adapter: ReportAdapter

    private lateinit var emptyStateTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        recyclerView = findViewById(R.id.recyclerViewReports)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReportAdapter(emptyList()) { report ->
            val intent = Intent(this, ReportDetailActivity::class.java)
            intent.putExtra("reportId", report.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        emptyStateTextView = findViewById(R.id.tvEmptyState)



        viewModel = AdminViewModel()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    when (state) {
                        is AdminState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                            emptyStateTextView.visibility = View.GONE

                        }

                        is AdminState.Success -> {
                            progressBar.visibility = View.GONE

                            if (state.reports.isEmpty()) {
                                recyclerView.visibility = View.GONE
                                emptyStateTextView.visibility = View.VISIBLE
                            } else {
                                recyclerView.visibility = View.VISIBLE
                                emptyStateTextView.visibility = View.GONE
                                adapter.updateList(state.reports)
                            }
                        }

                        is AdminState.Error -> {
                            progressBar.visibility = View.GONE
                            recyclerView.visibility = View.GONE
                            emptyStateTextView.visibility = View.VISIBLE
                            emptyStateTextView.text = state.message
                        }
                    }
                }

            }
        }
        viewModel.loadReports()
    }
}
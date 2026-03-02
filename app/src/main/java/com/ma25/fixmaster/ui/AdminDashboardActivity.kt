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
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.ma25.fixmaster.LoginActivity
import com.ma25.fixmaster.R
import com.ma25.fixmaster.model.AdminFilter
import kotlinx.coroutines.launch

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var viewModel: AdminViewModel
    private lateinit var adapter: ReportAdapter

    private lateinit var emptyStateTextView: TextView
    private lateinit var btnLogoutAdmin: MaterialButton

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        recyclerView = findViewById(R.id.recyclerViewReports)
        progressBar = findViewById(R.id.progressBar)
        emptyStateTextView = findViewById(R.id.tvEmptyState)
        btnLogoutAdmin = findViewById(R.id.btnLogoutAdmin)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReportAdapter(emptyList()) { report ->
            val intent = Intent(this, ReportDetailActivity::class.java)
            intent.putExtra("reportId", report.id)
            intent.putExtra("currentStatus", report.status)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        emptyStateTextView = findViewById(R.id.tvEmptyState)



        viewModel = AdminViewModel()

        val chipGroup = findViewById<com.google.android.material.chip.ChipGroup>(
            R.id.chipGroupFilters
        )

        chipGroup.setOnCheckedStateChangeListener {_,checkedIds ->
            when (checkedIds.firstOrNull()){

                R.id.chipAll ->
                    viewModel.setFilter(AdminFilter.ALL)

                R.id.chipHigh ->
                    viewModel.setFilter(AdminFilter.HIGH_PRIORITY)

                R.id.chipFloor1 ->
                    viewModel.setFilter(AdminFilter.FLOOR_1)

                R.id.chipIT ->
                    viewModel.setFilter(AdminFilter.IT)

                R.id.chipWater ->
                    viewModel.setFilter(AdminFilter.WATER)
            }
        }

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
                                emptyStateTextView.text = "Inga ärenden hittades"
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


        btnLogoutAdmin.setOnClickListener {




            auth.signOut()

            // 3) Navigate to Login
            val i = Intent(this, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }
    }
}
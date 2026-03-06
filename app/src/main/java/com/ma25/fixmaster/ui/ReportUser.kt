package com.ma25.fixmaster.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.ma25.fixmaster.R
import com.ma25.fixmaster.repository.ObjectRepository
import kotlinx.coroutines.launch

class ReportUser : AppCompatActivity() {

    private val repo = ObjectRepository()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var recycler: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var empty: TextView
    private lateinit var adapter: ReportAdapter
    private lateinit var btnBack: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_report)

        recycler = findViewById(R.id.recyclerViewReports)
        progress = findViewById(R.id.progressBar)
        empty = findViewById(R.id.tvEmptyState)
        btnBack = findViewById(R.id.btnBack)

        // ✅ Tillbaka
        btnBack.setOnClickListener {
            finish() // يرجع للشاشة السابقة (UserDashboard)
        }

        recycler.layoutManager = LinearLayoutManager(this)
        adapter = ReportAdapter(emptyList()) { /* click optional */ }
        recycler.adapter = adapter

        val uid = auth.currentUser?.uid
        if (uid == null) {
            progress.visibility = View.GONE
            recycler.visibility = View.GONE
            empty.visibility = View.VISIBLE
            empty.text = "Du är inte inloggad"
            return
        }

        progress.visibility = View.VISIBLE
        empty.visibility = View.GONE
        recycler.visibility = View.GONE

        lifecycleScope.launch {
            repo.observeMyReports(uid).collect { myReports ->
                progress.visibility = View.GONE

                if (myReports.isEmpty()) {
                    recycler.visibility = View.GONE
                    empty.visibility = View.VISIBLE
                    empty.text = "Inga ärenden än"
                } else {
                    empty.visibility = View.GONE
                    recycler.visibility = View.VISIBLE
                    adapter.updateList(myReports)
                }
            }
        }
    }
}
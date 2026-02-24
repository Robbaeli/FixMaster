package com.ma25.fixmaster.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ma25.fixmaster.R
import com.ma25.fixmaster.data.model.IssueReport


class ReportAdapter(
    private var reports: List<IssueReport>,
    private val onItemClick: (IssueReport) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvObjectName: TextView = itemView.findViewById(R.id.tvObjectName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvPriority: TextView = itemView.findViewById(R.id.tvPriority)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)

    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]

        holder.itemView.setOnClickListener {
            onItemClick(report)
        }

        holder.tvObjectName.text = report.objectName
        holder.tvLocation.text = report.objectId
        holder.tvStatus.text = report.status
        when (report.status) {
        "Ny"-> holder.tvStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.status_red)
        )
            "Påbörjad"-> holder.tvStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.status_yellow)
            )
            "Klar"-> holder.tvStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.status_green)
            )

        }

        holder.tvPriority.text = report.description
        holder.tvTime.text = "Just nu"
    }

    override fun getItemCount(): Int = reports.size

    fun updateList(newList: List<IssueReport>) {
        reports = newList
        notifyDataSetChanged()
    }
}
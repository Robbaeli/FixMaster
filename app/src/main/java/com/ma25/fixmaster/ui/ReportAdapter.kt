package com.ma25.fixmaster.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ma25.fixmaster.R
import com.ma25.fixmaster.model.IssueReport


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
            "Ny" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.status_new)
            holder.tvStatus.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.status_red)
            )
        }

        "Påbörjad"-> {
            holder.tvStatus.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.status_yellow)
            )
            holder.tvStatus.setTextColor(Color.BLACK)
        }

            "Klar"-> {
                holder.tvStatus.setBackgroundColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.status_green)
                )
                holder.tvStatus.setTextColor(Color.WHITE)
            }

        }

        holder.tvPriority.text = report.priority

        when (report.priority) {
            "HIGH" -> {
                holder.tvPriority.setBackgroundResource(R.drawable.priority_high)
                holder.tvPriority.setTextColor(Color.WHITE)

            }
            "MEDIUM" -> {
                holder.tvPriority.setBackgroundResource(R.drawable.priority_medium)
                holder.tvPriority.setTextColor(Color.BLACK)
            }
            else -> { //LOW
                holder.tvPriority.setBackgroundResource(R.drawable.priority_low)
                holder.tvPriority.setTextColor(Color.BLACK)
            }

        }

        val timeMillis = report.timestamp?.toDate()?.time

        if (timeMillis != null) {
            holder.tvTime.text =getTimeAgo(timeMillis)
        } else {
            holder.tvTime.text=""
        }
    }

    private fun getTimeAgo(timestamp: Long): String {
        val diff =System.currentTimeMillis() - timestamp

        val minutes = diff / (1000 * 60)
        val hours = diff / (1000 * 60 * 60)
        val days = diff / (1000 * 60 * 60 *24)

        return when {
            minutes < 1 -> "Just nu"
            minutes < 60 -> "$minutes min sedan"
            hours < 24 -> "$hours h sedan"
            else -> "$days dagar sedan"
        }

    }

    override fun getItemCount(): Int = reports.size

    fun updateList(newList: List<IssueReport>) {
        reports = newList
        notifyDataSetChanged()
    }
}
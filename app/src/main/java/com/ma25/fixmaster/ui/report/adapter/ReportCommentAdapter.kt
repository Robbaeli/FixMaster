package com.ma25.fixmaster.ui.report.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ma25.fixmaster.R
import com.ma25.fixmaster.model.ReportComment

class ReportCommentsAdapter(
    private var items: List<ReportComment>
) : RecyclerView.Adapter<ReportCommentsAdapter.VH>() {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRole: TextView = itemView.findViewById(R.id.tvCommentRole)
        val tvText: TextView = itemView.findViewById(R.id.tvCommentText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = items[position]
        holder.tvRole.text = c.authorRole.uppercase()
        holder.tvText.text = c.text
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<ReportComment>) {
        items = newItems
        notifyDataSetChanged()
    }
}
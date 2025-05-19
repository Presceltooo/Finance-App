package com.example.financeapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financeapp.Domain.BookmarkTransaction
import com.example.financeapp.databinding.ViewholderBookmarkBinding
import java.text.SimpleDateFormat
import java.util.*

class BookmarkAdapter(
    private val items: List<BookmarkTransaction>,
    private val onEdit: (BookmarkTransaction) -> Unit,
    private val onDelete: (BookmarkTransaction) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {
    class ViewHolder(val binding: ViewholderBookmarkBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.typeTxt.text = item.type
        holder.binding.amountTxt.text =
            (if (item.type == "DEPOSIT") "+" else "-") + "$" + String.format("%.2f", item.amount)
        holder.binding.methodTxt.text = item.method
        holder.binding.timeTxt.text = formatDateTime(item.scheduledTime)
        holder.binding.statusTxt.text = if (item.status == "DONE") "Done" else "Pending"
        // Màu số tiền
        if (item.type == "WITHDRAW") {
            holder.binding.amountTxt.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
        } else {
            holder.binding.amountTxt.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
        }
        // Hiển thị nút Edit/Delete cho Pending
        if (item.status == "PENDING") {
            holder.binding.btnEdit.visibility = android.view.View.VISIBLE
            holder.binding.btnDelete.visibility = android.view.View.VISIBLE
            holder.binding.btnEdit.setOnClickListener { onEdit(item) }
            holder.binding.btnDelete.setOnClickListener { onDelete(item) }
        } else {
            holder.binding.btnEdit.visibility = android.view.View.GONE
            holder.binding.btnDelete.visibility = android.view.View.GONE
        }
    }

    override fun getItemCount(): Int = items.size

    private fun formatDateTime(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(millis))
    }
} 
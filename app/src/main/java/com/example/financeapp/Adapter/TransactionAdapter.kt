package com.example.financeapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financeapp.Domain.TransactionDomain
import com.example.financeapp.databinding.ViewholderTransactionBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(private val transactions: MutableList<TransactionDomain>) : 
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderTransactionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        
        // Hiển thị loại giao dịch
        holder.binding.transactionType.text = transaction.type
        
        // Hiển thị phương thức thanh toán
        holder.binding.transactionMethod.text = transaction.method
        
        // Hiển thị số tiền
        val amountText = if (transaction.type == "DEPOSIT") "+" else "-"
        holder.binding.transactionAmount.text = "$amountText$${formatAmount(transaction.amount)}"
        
        // Hiển thị ngày giờ
        holder.binding.transactionDate.text = formatDate(transaction.date)
        
        // Thay đổi màu sắc dựa trên trạng thái
        when (transaction.status) {
            "SUCCESS" -> holder.binding.transactionAmount.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_green_dark)
            )
            "PENDING" -> holder.binding.transactionAmount.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_orange_dark)
            )
            "FAILED" -> holder.binding.transactionAmount.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_red_dark)
            )
        }
    }

    override fun getItemCount(): Int = transactions.size

    private fun formatAmount(amount: Double): String {
        return String.format("%.2f", amount)
    }

    private fun formatDate(date: java.util.Date): String {
        val today = java.util.Date()
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        
        return when {
            isToday(date) -> {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                "Today, ${timeFormat.format(date)}"
            }
            isYesterday(date) -> {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                "Yesterday, ${timeFormat.format(date)}"
            }
            else -> {
                val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                dateFormat.format(date)
            }
        }
    }

    private fun isToday(date: java.util.Date): Boolean {
        val today = java.util.Calendar.getInstance()
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        
        return today.get(java.util.Calendar.YEAR) == calendar.get(java.util.Calendar.YEAR) &&
                today.get(java.util.Calendar.DAY_OF_YEAR) == calendar.get(java.util.Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(date: java.util.Date): Boolean {
        val yesterday = java.util.Calendar.getInstance()
        yesterday.add(java.util.Calendar.DAY_OF_YEAR, -1)
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        
        return yesterday.get(java.util.Calendar.YEAR) == calendar.get(java.util.Calendar.YEAR) &&
                yesterday.get(java.util.Calendar.DAY_OF_YEAR) == calendar.get(java.util.Calendar.DAY_OF_YEAR)
    }
} 
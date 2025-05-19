package com.example.financeapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.financeapp.Domain.ExpenseDomain
import com.example.financeapp.databinding.ViewholderItemsBinding
import java.text.DecimalFormat

class ExpenseListAdapter(private val items: MutableList<ExpenseDomain>) : RecyclerView.Adapter<ExpenseListAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderItemsBinding) : RecyclerView.ViewHolder(binding.root)

    private lateinit var context: Context
    private var formatter: DecimalFormat? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        context = parent.context
        formatter = DecimalFormat("###,###,###.##")
        val binding = ViewholderItemsBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]

        holder.binding.titleTxt.text = item.title
        holder.binding.timeTxt.text = item.time
        holder.binding.priceTxt.text = "$" + formatter?.format(item.price)
        val drawableResourceId = holder.itemView.resources.getIdentifier(item.pic, "drawable", context.packageName)

        Glide.with(context)
            .load(drawableResourceId)
            .into(holder.binding.pic)
    }

    override fun getItemCount(): Int = items.size
}
package com.example.financeapp.Adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financeapp.Domain.BudgetDomain
import com.example.financeapp.databinding.ViewholderBudgetBinding
import java.text.DecimalFormat
import android.view.LayoutInflater
import com.example.financeapp.R

class ReportListAdapter(private val items: MutableList<BudgetDomain>): RecyclerView.Adapter<ReportListAdapter.ViewHolder>() {
    class ViewHolder (val binding: ViewholderBudgetBinding): RecyclerView.ViewHolder(binding.root)

    private lateinit var context: Context
    var formatter: DecimalFormat? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReportListAdapter.ViewHolder {
        context=parent.context
        formatter=DecimalFormat("###,###,###,###")
        val binding=ViewholderBudgetBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportListAdapter.ViewHolder, position: Int) {
        val item=items[position]

        holder.binding.titleTxt.text=item.title
        holder.binding.percentTxt.text="%"+item.percent
        holder.binding.priceTxt.text = "$" + formatter?.format(item.price) + " /Month"

        holder.binding.circularProgressBar.apply {
            progress=item.percent.toFloat()

            if(position%2==1){
                progressBarColor=context.resources.getColor(R.color.blue)
                holder.binding.percentTxt.setTextColor(context.resources.getColor(R.color.blue))
            }else{
                progressBarColor=context.resources.getColor(R.color.pink)
                holder.binding.percentTxt.setTextColor(context.resources.getColor(R.color.pink))
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
package com.victoriodev.anmpexpense.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victoriodev.anmpexpense.databinding.ReportListItemBinding
import com.victoriodev.anmpexpense.model.BudgetCategory

class ListReportAdapter : RecyclerView.Adapter<ListReportAdapter.ReportViewHolder>() {

    private var budgets: List<BudgetCategory> = emptyList()
    private var expenseMap: Map<Int, Int> = emptyMap()

    inner class ReportViewHolder(val binding: ReportListItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding  = ReportListItemBinding.inflate(inflater, parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val budget      = budgets[position]
        val totalUsed   = expenseMap[budget.uuid] ?: 0
        val maxBudget   = budget.nominal
        val remaining   = maxBudget - totalUsed
        val percent     = if (maxBudget == 0) 0 else (totalUsed * 100 / maxBudget)

        with(holder.binding) {
            txtKategori.text = budget.nama
            txtUsed.text     = "IDR ${formatIDR(totalUsed)}"
            txtMax.text      = "IDR ${formatIDR(maxBudget)}"
            txtSisa.text     = "Budget left: IDR ${formatIDR(remaining)}"

            progressBudget.max      = 100
            progressBudget.progress = percent
        }
    }

    override fun getItemCount(): Int = budgets.size

    fun updateData(newBudgets: List<BudgetCategory>, newExpenseMap: Map<Int, Int>) {
        budgets     = newBudgets
        expenseMap  = newExpenseMap
        notifyDataSetChanged()
    }

    private fun formatIDR(num: Int): String =
        "%,d".format(num).replace(',', '.')
}
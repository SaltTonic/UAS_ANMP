package com.victoriodev.anmpexpense.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victoriodev.anmpexpense.databinding.BudgetListItemBinding
import com.victoriodev.anmpexpense.model.BudgetCategory

class ListBudgetAdapter(private val budgetList: ArrayList<BudgetCategory>) :
    RecyclerView.Adapter<ListBudgetAdapter.BudgetViewHolder>() {

    class BudgetViewHolder(var binding: BudgetListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = BudgetListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BudgetViewHolder(binding)
    }

    override fun getItemCount(): Int = budgetList.size

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgetList[position]
        holder.binding.txtBudgetKategori.text = budget.nama
        holder.binding.txtNominalBudget.text = "Rp ${budget.nominal}"
    }

    fun updateBudgetList(newList: List<BudgetCategory>) {
        budgetList.clear()
        budgetList.addAll(newList)
        notifyDataSetChanged()
    }
}

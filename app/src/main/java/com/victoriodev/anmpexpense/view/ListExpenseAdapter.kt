package com.victoriodev.anmpexpense.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victoriodev.anmpexpense.databinding.ExpenseListItemBinding
import com.victoriodev.anmpexpense.model.Expense
import java.text.SimpleDateFormat
import java.util.*

class ListExpenseAdapter(private val expenseList: ArrayList<Expense>) :
    RecyclerView.Adapter<ListExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(val binding: ExpenseListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ExpenseListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun getItemCount(): Int = expenseList.size

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.binding.txtNominal.text = "Rp ${expense.nominal}"
        holder.binding.txtTanggalJam.text = formatDate(expense.date)
        holder.binding.chipKategori.text = "Kategori ID: ${expense.budgetCategoryId}" // Optional: Replace with nama kategori
    }

    fun updateExpenseList(newList: List<Expense>) {
        expenseList.clear()
        expenseList.addAll(newList)
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

package com.victoriodev.anmpexpense.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victoriodev.anmpexpense.databinding.ExpenseListItemBinding
import com.victoriodev.anmpexpense.model.Expense
import java.text.SimpleDateFormat
import java.util.*

class ListExpenseAdapter(
    private val expenseList: ArrayList<Expense>,
    private var budgetMap: Map<Int, String>,
    private val onChipClick: (Expense) -> Unit      // ⬅️ callback klik chip
) : RecyclerView.Adapter<ListExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(val binding: ExpenseListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ExpenseListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ExpenseViewHolder(binding)
    }

    override fun getItemCount(): Int = expenseList.size

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]

        holder.binding.txtNominal.text  = "Rp ${expense.nominal}"
        holder.binding.txtTanggalJam.text =
            SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(expense.date))

        val namaKategori = budgetMap[expense.budgetCategoryId] ?: "Kategori?"
        val chip = holder.binding.chipKategori
        chip.text = namaKategori

        chip.setOnClickListener {
            onChipClick(expense)            // ⬅️ panggil callback
        }
    }

    fun updateExpenseList(newList: List<Expense>, newMap: Map<Int, String>) {
        expenseList.clear()
        expenseList.addAll(newList)
        budgetMap = newMap
        notifyDataSetChanged()
    }
}

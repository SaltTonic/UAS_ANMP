package com.victoriodev.anmpexpense.view

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.victoriodev.anmpexpense.adapter.ListReportAdapter
import com.victoriodev.anmpexpense.databinding.FragmentListReportBinding
import com.victoriodev.anmpexpense.model.AppDatabase
import com.victoriodev.anmpexpense.util.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentListReport : Fragment() {

    private lateinit var binding: FragmentListReportBinding
    private lateinit var adapter: ListReportAdapter
    private var userId: Int = -1

    /* ---------------- lifecycle ---------------- */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = UserSession(requireContext()).getCurrentUserId()
        if (userId == -1) return

        adapter = ListReportAdapter()
        binding.recReport.layoutManager = LinearLayoutManager(requireContext())
        binding.recReport.setHasFixedSize(true)
        binding.recReport.adapter = adapter

        fetchReport()
    }


    private fun fetchReport() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase(requireContext())
            val budgets = db.budgetCategoryDao().selectAllTodo(userId)
            val expenses = db.expenseDao().selectAllTodo(userId)

            val expenseMap = expenses.groupBy { it.budgetCategoryId }
                .mapValues { entry -> entry.value.sumOf { it.nominal } }

            val totalExpense = expenseMap.values.sum()
            val totalBudget = budgets.sumOf { it.nominal }

            withContext(Dispatchers.Main) {
                adapter.updateData(budgets, expenseMap)


                val text = "Total Expenses / Budget\nIDR ${fmt(totalExpense)} / IDR ${fmt(totalBudget)}"
                binding.txtFooter.text = text
                binding.txtFooter.visibility = View.VISIBLE
            }
        }
    }

    private fun fmt(n: Int) = "%,d".format(n).replace(',', '.')
}

package com.victoriodev.anmpexpense.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.victoriodev.anmpexpense.R
import com.victoriodev.anmpexpense.databinding.FragmentListExpenseBinding
import com.victoriodev.anmpexpense.model.Expense
import com.victoriodev.anmpexpense.util.UserSession
import com.victoriodev.anmpexpense.viewmodel.ListTodoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FragmentListExpense : Fragment() {
    private lateinit var binding: FragmentListExpenseBinding
    private lateinit var viewModel: ListTodoViewModel
    private lateinit var userSession: UserSession
    private lateinit var adapter: ListExpenseAdapter
    private var latestBudgetMap: Map<Int, String> = mapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userSession = UserSession(requireContext())
        val userId = userSession.getCurrentUserId()

        viewModel = ViewModelProvider(this)[ListTodoViewModel::class.java]
        viewModel.refresh(userId)

        adapter = ListExpenseAdapter(arrayListOf(), latestBudgetMap) { expense ->
            showExpenseDialog(expense)
        }

        binding.recyclerViewExpense.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewExpense.adapter = adapter

        binding.btnFabExpense.setOnClickListener {
            val action = FragmentListExpenseDirections.actionToNewExpense()
            Navigation.findNavController(it).navigate(action)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.budgetCategoryLD.observe(viewLifecycleOwner) { budgetList ->
            latestBudgetMap = budgetList.associate { it.uuid to it.nama }
            viewModel.expenseLD.value?.let { setOrCreateAdapter(it) }
        }

        viewModel.expenseLD.observe(viewLifecycleOwner) { expenseList ->
            setOrCreateAdapter(expenseList)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressLoadExpense.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isError.observe(viewLifecycleOwner) {
            binding.txtErrorExpense.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun setOrCreateAdapter(expenses: List<Expense>) {
        if (!::adapter.isInitialized) {
            adapter = ListExpenseAdapter(
                ArrayList(expenses),
                latestBudgetMap
            ) { expense -> showExpenseDialog(expense) }
            binding.recyclerViewExpense.adapter = adapter
        } else {
            adapter.updateExpenseList(expenses, latestBudgetMap)
        }
    }


    private fun showExpenseDialog(expense: Expense) {
        val dialogView = layoutInflater.inflate(R.layout.expense_item_detail, null)

        // Format tanggal
        val formattedDate = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            .format(Date(expense.date))

        // Set data ke komponen dialog
        dialogView.findViewById<TextView>(R.id.txtDialogDate).text = formattedDate
        dialogView.findViewById<TextView>(R.id.txtDialogNominal).text = "Rp ${expense.nominal}"
        dialogView.findViewById<TextView>(R.id.txtDialogNote).text = expense.name
        dialogView.findViewById<TextView>(R.id.txtDialogCategory).text =
            "Kategori: ${latestBudgetMap[expense.budgetCategoryId] ?: "-"}"

        // Bangun dialog
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Tombol close
        dialogView.findViewById<Button>(R.id.btnDialogClose).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

}



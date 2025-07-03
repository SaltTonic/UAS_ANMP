package com.victoriodev.anmpexpense.view

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.victoriodev.anmpexpense.databinding.FragmentNewExpenseBinding
import com.victoriodev.anmpexpense.model.AppDatabase
import com.victoriodev.anmpexpense.model.BudgetCategory
import com.victoriodev.anmpexpense.model.Expense
import com.victoriodev.anmpexpense.util.UserSession
import com.victoriodev.anmpexpense.viewmodel.DetailTodoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class FragmentNewExpense : Fragment() {

    private lateinit var binding: FragmentNewExpenseBinding
    private lateinit var viewModel: DetailTodoViewModel
    private lateinit var userSession: UserSession

    private var budgetList: List<BudgetCategory> = emptyList()
    private var selectedBudget: BudgetCategory? = null
    private var userId: Int = -1   // di‑set saat onViewCreated

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userSession = UserSession(requireContext())
        userId = userSession.getCurrentUserId()

        if (userId == -1) {
            toast("Silakan login terlebih dahulu")
            return
        }

        viewModel = ViewModelProvider(this)[DetailTodoViewModel::class.java]

        binding.txtDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        // --- Muat list kategori budget milik user login ---
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase(requireContext())
            budgetList = db.budgetCategoryDao().selectAllTodo(userId)

            withContext(Dispatchers.Main) {
                val spinnerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    budgetList.map { it.nama }
                )
                binding.spinnerKategori.adapter = spinnerAdapter
            }
        }

        binding.spinnerKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, v: View?, position: Int, id: Long
            ) {
                selectedBudget = budgetList[position]
                updateProgressUI()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.btnAddExpense.setOnClickListener { addExpense() }
    }

    /* ------------------- Tambah Expense ------------------- */
    private fun addExpense() {
        val nominalStr = binding.txtInputNewNominal.text.toString().trim()
        val note       = binding.txtInputNewNote.text.toString().trim()
        val budget     = selectedBudget

        if (budget == null || nominalStr.isBlank()) {
            toast("Lengkapi semua data")
            return
        }

        val nominal = nominalStr.toIntOrNull()
        if (nominal == null || nominal <= 0) {
            toast("Nominal harus berupa angka > 0")
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val used = getUsedAmountForCategory(budget.uuid)
            val remaining = budget.nominal - used

            if (nominal > remaining) {
                withContext(Dispatchers.Main) {
                    toast("Nominal melebihi sisa budget (Rp $remaining)")
                }
                return@launch
            }

            val expense = Expense(
                name = if (note.isBlank()) "Expense" else note,
                nominal = nominal,
                date = System.currentTimeMillis(),
                budgetCategoryId = budget.uuid,
                userId = userId                // <‑‑ pakai userId dari session
            )
            viewModel.addExpense(listOf(expense))

            withContext(Dispatchers.Main) {
                toast("Expense ditambahkan")
                binding.txtInputNewNominal.text?.clear()
                binding.txtInputNewNote.text?.clear()
                updateProgressUI()
            }
        }
    }

    /* ------------------- Progress & helper ------------------- */
    private fun updateProgressUI() {
        val budget = selectedBudget ?: return
        lifecycleScope.launch(Dispatchers.IO) {
            val used = getUsedAmountForCategory(budget.uuid)
            val max  = budget.nominal
            val pct  = if (max == 0) 0 else (used * 100 / max)

            withContext(Dispatchers.Main) {
                binding.progressBar.progress = pct
                binding.txtUsedNew.text = "Used: Rp $used"
                binding.txtMaxNew.text  = "Max: Rp $max"
            }
        }
    }

    private suspend fun getUsedAmountForCategory(catId: Int): Int {
        val db = AppDatabase(requireContext())
        return db.expenseDao().getTotalExpenseByBudget(catId) ?: 0
    }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}
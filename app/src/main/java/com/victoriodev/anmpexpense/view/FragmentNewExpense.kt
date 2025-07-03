package com.victoriodev.anmpexpense.view

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.victoriodev.anmpexpense.databinding.FragmentNewExpenseBinding
import com.victoriodev.anmpexpense.model.AppDatabase
import com.victoriodev.anmpexpense.model.BudgetCategory
import com.victoriodev.anmpexpense.model.Expense
import com.victoriodev.anmpexpense.viewmodel.DetailTodoViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class FragmentNewExpense : Fragment() {
    private lateinit var binding: FragmentNewExpenseBinding
    private lateinit var viewModel: DetailTodoViewModel
    private var budgetList: List<BudgetCategory> = listOf()
    private var selectedBudgetId: Int? = null
    private val userId = 1 // Sesuaikan dengan user login nanti

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[DetailTodoViewModel::class.java]

        // Tampilkan tanggal hari ini
        val currentDate = Date()
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        binding.txtDate.text = sdf.format(currentDate)

        // Ambil data kategori budget dari DB
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase(requireContext())
            budgetList = db.budgetCategoryDao().selectAllTodo(userId)
            val spinnerItems = budgetList.map { it.nama }

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    spinnerItems
                )
                binding.spinnerKategori.adapter = adapter

                binding.spinnerKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        selectedBudgetId = budgetList[position].uuid
                        val selectedBudget = budgetList[position]

                        CoroutineScope(Dispatchers.IO).launch {
                            val used = getUsedAmountForCategory(selectedBudget.uuid)
                            val max = selectedBudget.nominal

                            val progress = if (max == 0) 0 else (used * 100 / max)

                            withContext(Dispatchers.Main) {
                                binding.progressBar.progress = progress
                                binding.txtUsedNew.text = "Used: Rp $used"
                                binding.txtMaxNew.text = "Max: Rp $max"
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }

            }
        }

        binding.btnAddExpense.setOnClickListener {
            val nominalText = binding.txtInputNewNominal.text.toString()
            val note = binding.txtInputNewNote.text.toString()
            val date = System.currentTimeMillis()

            if (nominalText.isNotBlank() && selectedBudgetId != null) {
                val nominal = nominalText.toIntOrNull()
                if (nominal != null) {
                    val selectedBudget = budgetList.first { it.uuid == selectedBudgetId }

                    // Cek jika nominal expense melebihi budget
                    if (nominal > selectedBudget.nominal) {
                        Toast.makeText(requireContext(), "Nominal melebihi sisa budget", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // Buat data expense
                    val newExpense = Expense(
                        name = note,
                        nominal = nominal,
                        date = date,
                        budgetCategoryId = selectedBudgetId!!,
                        userId = userId
                    )

                    // Simpan expense
                    viewModel.addExpense(listOf(newExpense))

                    // Update budget - kurangi nilai nominal budget
                    selectedBudget.nominal -= nominal
                    viewModel.updateBudgetCategory(selectedBudget.uuid, selectedBudget.nominal)

                    Toast.makeText(requireContext(), "Expense ditambahkan", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                } else {
                    Toast.makeText(requireContext(), "Nominal harus berupa angka", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Lengkapi semua data", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private suspend fun getUsedAmountForCategory(categoryId: Int): Int {
        val db = AppDatabase(requireContext())
        val expenses = db.expenseDao().selectAllByCategory(categoryId)
        return expenses.sumOf { it.nominal }
    }

}

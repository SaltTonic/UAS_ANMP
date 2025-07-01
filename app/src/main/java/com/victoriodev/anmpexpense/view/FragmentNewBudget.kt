package com.victoriodev.anmpexpense.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.victoriodev.anmpexpense.databinding.FragmentNewBudgetBinding
import com.victoriodev.anmpexpense.model.BudgetCategory
import com.victoriodev.anmpexpense.viewmodel.DetailTodoViewModel

class FragmentNewBudget : Fragment() {
    private lateinit var binding: FragmentNewBudgetBinding
    private lateinit var viewModel: DetailTodoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[DetailTodoViewModel::class.java]

        binding.btnAddNewBudget.setOnClickListener {
            val nama = binding.txtTitleBudget.text.toString()
            val nominalText = binding.txtxInputBudgetNominalNew.text.toString()

            if (nama.isNotBlank() && nominalText.isNotBlank()) {
                val nominal = nominalText.toIntOrNull()
                if (nominal != null) {
                    val newBudget = BudgetCategory(
                        nama = nama,
                        nominal = nominal,
                        userId = 1
                    )
                    viewModel.addBudgetCategory(listOf(newBudget))

                    Toast.makeText(requireContext(), "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                } else {
                    Toast.makeText(requireContext(), "Nominal harus berupa angka", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

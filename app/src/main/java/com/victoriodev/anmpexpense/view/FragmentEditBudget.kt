package com.victoriodev.anmpexpense.view

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.victoriodev.anmpexpense.databinding.FragmentEditBudgetBinding
import com.victoriodev.anmpexpense.viewmodel.DetailTodoViewModel

class FragmentEditBudget : Fragment() {
    private lateinit var binding: FragmentEditBudgetBinding
    private lateinit var viewModel: DetailTodoViewModel
    private var uuid: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[DetailTodoViewModel::class.java]
        uuid = arguments?.getInt("uuid", 0) ?: 0

        binding.btnUpdateBudget.setOnClickListener {
            val nama = binding.txtInputEditBudgetName.text.toString().trim()
            val nominalStr = binding.txtInputEditBudgetNominal.text.toString().trim()

            when {
                nama.isBlank() || nominalStr.isBlank() -> {
                    showToast("Mohon isi semua data")
                }
                nominalStr.toIntOrNull() == null -> {
                    showToast("Nominal harus berupa angka")
                }
                else -> {
                    val nominal = nominalStr.toInt()
                    viewModel.updateBudgetCategoryFull(uuid, nama, nominal) { sukses ->
                        requireActivity().runOnUiThread {
                            if (sukses) {
                                showToast("Budget berhasil diperbarui")
                                requireActivity().onBackPressed()
                            } else {
                                showToast("Nominal baru di bawah total pengeluaran kategori ini!")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

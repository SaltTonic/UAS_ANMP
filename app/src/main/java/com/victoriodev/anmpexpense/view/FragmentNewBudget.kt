package com.victoriodev.anmpexpense.view

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.victoriodev.anmpexpense.databinding.FragmentNewBudgetBinding
import com.victoriodev.anmpexpense.model.BudgetCategory
import com.victoriodev.anmpexpense.util.UserSession
import com.victoriodev.anmpexpense.viewmodel.DetailTodoViewModel

class FragmentNewBudget : Fragment() {

    private lateinit var binding: FragmentNewBudgetBinding
    private lateinit var viewModel: DetailTodoViewModel
    private lateinit var userSession: UserSession
    private var userId: Int = -1

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
        userSession = UserSession(requireContext())
        userId = userSession.getCurrentUserId()

        if (userId == -1) {
            Toast.makeText(requireContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnAddNewBudget.setOnClickListener {
            val nama = binding.txtInputNameBudgetNew.text.toString().trim()
            val nominalText = binding.txtxInputBudgetNominalNew.text.toString().trim()

            if (nama.isNotBlank() && nominalText.isNotBlank()) {
                val nominal = nominalText.toIntOrNull()
                if (nominal != null) {
                    val newBudget = BudgetCategory(
                        nama = nama,
                        nominal = nominal,
                        userId = userId
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
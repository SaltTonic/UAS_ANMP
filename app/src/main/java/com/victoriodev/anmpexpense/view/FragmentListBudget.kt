package com.victoriodev.anmpexpense.view

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.victoriodev.anmpexpense.databinding.FragmentListBudgetBinding
import com.victoriodev.anmpexpense.util.UserSession
import com.victoriodev.anmpexpense.viewmodel.ListTodoViewModel

class FragmentListBudget : Fragment() {

    private lateinit var binding: FragmentListBudgetBinding
    private lateinit var viewModel: ListTodoViewModel
    private lateinit var userSession: UserSession
    private lateinit var adapter: ListBudgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userSession = UserSession(requireContext())
        val userId = userSession.getCurrentUserId()

        if (userId == -1) {
            Toast.makeText(requireContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel = ViewModelProvider(this)[ListTodoViewModel::class.java]
        viewModel.refresh(userId)

        adapter = ListBudgetAdapter(arrayListOf()) { budget ->
            val action = FragmentListBudgetDirections.actionEditBudgetTodo(
                uuid = budget.uuid,
                nama = budget.nama,
                nominal = budget.nominal
            )
            Navigation.findNavController(view).navigate(action)
        }

        binding.recyclerViewBudget.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBudget.adapter = adapter

        binding.btnFabBudget.setOnClickListener {
            if (userId == -1) {
                Toast.makeText(requireContext(), "Tidak dapat menambahkan budget. Login terlebih dahulu.", Toast.LENGTH_SHORT).show()
            } else {
                val action = FragmentListBudgetDirections.actionCreateBudgetTodo()
                Navigation.findNavController(it).navigate(action)
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.budgetCategoryLD.observe(viewLifecycleOwner) { list ->
            adapter.updateBudgetList(list)
        }
    }

    override fun onResume() {
        super.onResume()
        val userId = userSession.getCurrentUserId()
        if (userId != -1) {
            viewModel.refresh(userId)
        }
    }
}

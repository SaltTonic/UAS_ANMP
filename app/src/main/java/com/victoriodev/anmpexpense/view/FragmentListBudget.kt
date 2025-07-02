package com.victoriodev.anmpexpense.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.victoriodev.anmpexpense.databinding.FragmentListBudgetBinding
import com.victoriodev.anmpexpense.util.UserSession
import com.victoriodev.anmpexpense.viewmodel.ListTodoViewModel

class FragmentListBudget : Fragment() {
    private lateinit var viewModel: ListTodoViewModel
    private lateinit var userSession: UserSession
    private lateinit var binding: FragmentListBudgetBinding
    private val listBudgetAdapter = ListBudgetAdapter(arrayListOf())

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

        viewModel = ViewModelProvider(this)[ListTodoViewModel::class.java]
        viewModel.refresh(userId)

        binding.recyclerViewBudget.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewBudget.adapter = listBudgetAdapter

        binding.btnFabBudget.setOnClickListener {
            val action = FragmentListBudgetDirections.actionCreateBudgetTodo()
            Navigation.findNavController(it).navigate(action)
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.budgetCategoryLD.observe(viewLifecycleOwner) {
            listBudgetAdapter.updateBudgetList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh(userSession.getCurrentUserId())
    }
}

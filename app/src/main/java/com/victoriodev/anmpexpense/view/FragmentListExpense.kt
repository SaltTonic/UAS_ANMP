package com.victoriodev.anmpexpense.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.victoriodev.anmpexpense.databinding.FragmentListExpenseBinding
import com.victoriodev.anmpexpense.util.UserSession
import com.victoriodev.anmpexpense.viewmodel.ListTodoViewModel

class FragmentListExpense : Fragment() {
    private lateinit var binding: FragmentListExpenseBinding
    private lateinit var viewModel: ListTodoViewModel
    private lateinit var userSession: UserSession
    private val adapter = ListExpenseAdapter(arrayListOf())

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

        binding.recyclerViewExpense.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewExpense.adapter = adapter

        binding.btnFabExpense.setOnClickListener {
            val action = FragmentListExpenseDirections.actionToNewExpense()
            Navigation.findNavController(it).navigate(action)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.expenseLD.observe(viewLifecycleOwner) {
            adapter.updateExpenseList(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressLoadExpense.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isError.observe(viewLifecycleOwner) {
            binding.txtErrorExpense.visibility = if (it) View.VISIBLE else View.GONE
        }
    }
}

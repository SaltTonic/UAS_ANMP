package com.victoriodev.anmpexpense.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.victoriodev.anmpexpense.R
import com.victoriodev.anmpexpense.databinding.FragmentListBudgetBinding
import com.victoriodev.anmpexpense.viewmodel.ListTodoViewModel

class FragmentListBudget : Fragment() {
    private lateinit var viewModel:ListTodoViewModel
    private val listBudgetAdapter = ListBudgetAdapter(arrayListOf())
    private lateinit var  binding:FragmentListBudgetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBudgetBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ListTodoViewModel::class.java)
        viewModel.refresh(1) //userid ne masih sementara soale masih belum disambungin ke login :)
        binding.recyclerViewBudget.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewBudget.adapter = listBudgetAdapter

        binding.btnFabBudget.setOnClickListener {
            val action = FragmentListBudgetDirections.actionCreateBudgetTodo()
            Navigation.findNavController(it).navigate(action)

            observeViewModel()
        }

    }

    private fun observeViewModel(){
        viewModel.budgetCategoryLD.observe(viewLifecycleOwner){
            listBudgetAdapter.updateBudgetList(it)
        }
    }
}
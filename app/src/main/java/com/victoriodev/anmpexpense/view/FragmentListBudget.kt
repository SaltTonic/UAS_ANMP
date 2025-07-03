package com.victoriodev.anmpexpense.view

import android.os.Bundle
import android.view.*
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
    private lateinit var adapter: ListBudgetAdapter  // adapter dideklarasi di sini

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

        // ViewModel
        viewModel = ViewModelProvider(this)[ListTodoViewModel::class.java]
        viewModel.refresh(userId)

        // Inisialisasi adapter dengan callback klik
        adapter = ListBudgetAdapter(arrayListOf()) { budget ->
            // SafeArgs action – pastikan sudah terdefinisi di nav_graph
            val action = FragmentListBudgetDirections.actionEditBudgetTodo(
                uuid = budget.uuid,
                nama = budget.nama,
                nominal = budget.nominal
            )

            Navigation.findNavController(view).navigate(action)

        }

        binding.recyclerViewBudget.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBudget.adapter = adapter

        // FAB tambah budget
        binding.btnFabBudget.setOnClickListener {
            val action = FragmentListBudgetDirections.actionCreateBudgetTodo()
            Navigation.findNavController(it).navigate(action)
        }

        observeViewModel()
    }

    /** Observe LiveData dari ViewModel */
    private fun observeViewModel() {
        viewModel.budgetCategoryLD.observe(viewLifecycleOwner) { list ->
            adapter.updateBudgetList(list)
        }
    }

    /** Refresh ulang saat fragment kembali aktif */
    override fun onResume() {
        super.onResume()
        viewModel.refresh(userSession.getCurrentUserId())
    }
}

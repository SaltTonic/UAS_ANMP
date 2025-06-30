package com.victoriodev.anmpexpense.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.victoriodev.anmpexpense.R
import com.victoriodev.anmpexpense.databinding.FragmentListBudgetBinding

class FragmentListBudget : Fragment() {
    private lateinit var  binding:FragmentListBudgetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {

    }
}
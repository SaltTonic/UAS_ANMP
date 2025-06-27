package com.victoriodev.anmpexpense.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.victoriodev.anmpexpense.R
import com.victoriodev.anmpexpense.databinding.FragmentLoginBinding


class FragmentLogin : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSignUp.setOnClickListener {
            val action = FragmentLoginDirections.actionNewAccountFragment()
            Navigation.findNavController(it).navigate(action)

        }
    }


    override fun onCreateView( inflater: LayoutInflater, container:
    ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(
            inflater,
            container, false
        )
        return binding.root
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            FragmentLogin().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
package com.victoriodev.anmpexpense.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.victoriodev.anmpexpense.databinding.FragmentNewAccountBinding
import com.victoriodev.anmpexpense.util.RegisterResult
import com.victoriodev.anmpexpense.viewmodel.AuthViewModel

class FragmentNewAccount : Fragment() {
    private lateinit var binding: FragmentNewAccountBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        authViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RegisterResult.Success -> {
                    Toast.makeText(requireContext(), "Account created successfully!", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                }
                is RegisterResult.UsernameExists -> {
                    Toast.makeText(requireContext(), "Username already exists. Please choose another.", Toast.LENGTH_SHORT).show()
                }
                is RegisterResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                null -> { /* Do nothing */ }
            }
        }

        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnCreateAccount.isEnabled = !isLoading
            binding.btnCancel.isEnabled = !isLoading

            if (isLoading) {
                binding.btnCreateAccount.text = "Creating Account..."
            } else {
                binding.btnCreateAccount.text = "Create Account"
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            navigateToLogin()
        }

        binding.btnCreateAccount.setOnClickListener {
            val username = binding.txtNewUsername.text.toString().trim()
            val firstname = binding.txtFirstName.text.toString().trim()
            val lastname = binding.txtLastName.text.toString().trim()
            val password = binding.txtNewPassword.text.toString()
            val repeatPassword = binding.txtRepeatPassword.text.toString()

            authViewModel.register(username, firstname, lastname, password, repeatPassword)
        }
    }

    private fun navigateToLogin() {
        val action = FragmentNewAccountDirections.actionLoginFragment()
        Navigation.findNavController(binding.root).navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        authViewModel.clearResults()
    }

    companion object {
        @JvmStatic
        fun newInstance() = FragmentNewAccount().apply {
            arguments = Bundle().apply {
                // Add arguments if needed
            }
        }
    }
}
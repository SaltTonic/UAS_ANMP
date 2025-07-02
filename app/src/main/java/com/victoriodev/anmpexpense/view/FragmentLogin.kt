package com.victoriodev.anmpexpense.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.victoriodev.anmpexpense.databinding.FragmentLoginBinding
import com.victoriodev.anmpexpense.util.LoginResult
import com.victoriodev.anmpexpense.viewmodel.AuthViewModel

class FragmentLogin : Fragment() {
    private lateinit var binding: FragmentLoginBinding
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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()

        // Check if user is already logged in
        if (authViewModel.checkSession()) {
            navigateToMain()
        }
    }

    private fun setupObservers() {
        authViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginResult.Success -> {
                    Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is LoginResult.InvalidCredentials -> {
                    Toast.makeText(requireContext(), "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
                is LoginResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                null -> { /* Do nothing */ }
            }
        }

        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnSignIn.isEnabled = !isLoading
            binding.btnSignUp.isEnabled = !isLoading

            if (isLoading) {
                binding.btnSignIn.text = "Signing In..."
            } else {
                binding.btnSignIn.text = "Sign In"
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSignUp.setOnClickListener {
            val action = FragmentLoginDirections.actionNewAccountFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnSignIn.setOnClickListener {
            val username = binding.txtUsername.text.toString().trim()
            val password = binding.txtPassword.text.toString()

            authViewModel.login(username, password)
        }
    }

    private fun navigateToMain() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        authViewModel.clearResults()
    }

    companion object {
        @JvmStatic
        fun newInstance() = FragmentLogin().apply {
            arguments = Bundle().apply {
                // Add arguments if needed
            }
        }
    }
}
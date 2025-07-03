package com.victoriodev.anmpexpense.view

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.victoriodev.anmpexpense.databinding.FragmentProfileBinding
import com.victoriodev.anmpexpense.model.AppDatabase
import com.victoriodev.anmpexpense.util.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentProfile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var userSession: UserSession
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userSession = UserSession(requireContext())
        userId = userSession.getCurrentUserId()

        if (userId == -1) {
            Toast.makeText(requireContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSignOut.setOnClickListener {
            userSession.logoutUser()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.btnChangePassword.setOnClickListener {
            val oldPass = binding.txtOldPassword.text?.toString()?.trim() ?: ""
            val newPass = binding.txtNewPassword.text?.toString()?.trim() ?: ""
            val repeatPass = binding.txtRepeatPassword.text?.toString()?.trim() ?: ""

            if (oldPass.isEmpty() || newPass.isEmpty() || repeatPass.isEmpty()) {
                showToast("Semua kolom harus diisi")
            } else if (newPass != repeatPass) {
                showToast("Password baru dan konfirmasi tidak sama")
            } else {
                updatePassword(oldPass, newPass)
            }
        }
    }

    private fun updatePassword(oldPassword: String, newPassword: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase(requireContext())
            val user = db.userDao().getUserByID(userId)

            if (user == null) {
                withContext(Dispatchers.Main) {
                    showToast("User tidak ditemukan")
                }
                return@launch
            }

            if (user.password != oldPassword) {
                withContext(Dispatchers.Main) {
                    showToast("Password lama salah")
                }
                return@launch
            }

            db.userDao().updatePassword(userId, newPassword)

            withContext(Dispatchers.Main) {
                showToast("Password berhasil diubah")
                clearFields()
            }
        }
    }

    private fun clearFields() {
        binding.txtOldPassword.text?.clear()
        binding.txtNewPassword.text?.clear()
        binding.txtRepeatPassword.text?.clear()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
package com.victoriodev.anmpexpense.util

import android.content.Context
import com.victoriodev.anmpexpense.model.AppDatabase
import com.victoriodev.anmpexpense.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {
    private val database = AppDatabase(context)
    private val userDao = database.userDao()

    suspend fun registerUser(username: String, firstname: String, lastname: String, password: String): RegisterResult {
        return withContext(Dispatchers.IO) {
            try {
                // Check if username already exists
                val existingUsers = userDao.getAllUsers()
                val usernameExists = existingUsers.any { it.username.equals(username, ignoreCase = true) }

                if (usernameExists) {
                    return@withContext RegisterResult.UsernameExists
                }

                // Create new user
                val newUser = User(
                    username = username,
                    firstname = firstname,
                    lastname = lastname,
                    password = password
                )

                userDao.insertAll(newUser)
                RegisterResult.Success
            } catch (e: Exception) {
                RegisterResult.Error(e.message ?: "Registration failed")
            }
        }
    }

    suspend fun loginUser(username: String, password: String): LoginResult {
        return withContext(Dispatchers.IO) {
            try {
                val users = userDao.getAllUsers()
                val user = users.find {
                    it.username.equals(username, ignoreCase = true) && it.password == password
                }

                if (user != null) {
                    LoginResult.Success(user)
                } else {
                    LoginResult.InvalidCredentials
                }
            } catch (e: Exception) {
                LoginResult.Error(e.message ?: "Login failed")
            }
        }
    }
}

sealed class RegisterResult {
    object Success : RegisterResult()
    object UsernameExists : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    object InvalidCredentials : LoginResult()
    data class Error(val message: String) : LoginResult()
}
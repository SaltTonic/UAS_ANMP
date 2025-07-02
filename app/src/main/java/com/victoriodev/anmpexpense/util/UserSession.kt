package com.victoriodev.anmpexpense.util

import android.content.Context
import android.content.SharedPreferences

class UserSession(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("expense_tracker_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "user_logged_in"
        private const val KEY_USER_ID = "current_user_id"
        private const val KEY_USERNAME = "current_username"
        private const val KEY_FIRSTNAME = "current_firstname"
        private const val KEY_LASTNAME = "current_lastname"
    }

    fun saveUserSession(userId: Int, username: String, firstname: String, lastname: String) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putInt(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putString(KEY_FIRSTNAME, firstname)
            putString(KEY_LASTNAME, lastname)
            apply()
        }
    }

    fun isUserLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getCurrentUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun getCurrentUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun getCurrentFirstname(): String? = prefs.getString(KEY_FIRSTNAME, null)

    fun getCurrentLastname(): String? = prefs.getString(KEY_LASTNAME, null)

    fun logoutUser() {
        prefs.edit().clear().apply()
    }

    fun hasValidSession(): Boolean {
        return isUserLoggedIn() && getCurrentUserId() != -1
    }
}
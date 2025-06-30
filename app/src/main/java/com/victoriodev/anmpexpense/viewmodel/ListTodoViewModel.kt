package com.victoriodev.anmpexpense.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.victoriodev.anmpexpense.model.AppDatabase
import com.victoriodev.anmpexpense.model.BudgetCategory
import com.victoriodev.anmpexpense.model.Expense
import com.victoriodev.anmpexpense.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ListTodoViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    val userLD = MutableLiveData<User>()
    val budgetCategoryLD = MutableLiveData<List<BudgetCategory>>()
    val expenseLD = MutableLiveData<List<Expense>>()

    val isLoading = MutableLiveData<Boolean>()
    val isError = MutableLiveData<Boolean>()

    fun refresh(userId: Int) {
        isLoading.value = true
        isError.value = false

        launch {
            try {
                val db = AppDatabase(getApplication())

                val user = db.userDao().getUserByID(userId)
                val budgetList = db.budgetCategoryDao().selectAllTodo(userId)
                val expenseList = db.expenseDao().selectAllTodo(userId)

                userLD.postValue(user)
                budgetCategoryLD.postValue(budgetList)
                expenseLD.postValue(expenseList)
                isLoading.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
                isError.postValue(true)
                isLoading.postValue(false)
            }
        }
    }
}

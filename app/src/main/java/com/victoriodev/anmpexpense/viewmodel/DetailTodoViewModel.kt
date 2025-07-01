package com.victoriodev.anmpexpense.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.victoriodev.anmpexpense.model.AppDatabase
import com.victoriodev.anmpexpense.model.BudgetCategory
import com.victoriodev.anmpexpense.model.Expense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DetailTodoViewModel(application: Application)
    : AndroidViewModel(application), CoroutineScope {

    private val job = Job()

    // Untuk BudgetCategory
    fun addBudgetCategory(list: List<BudgetCategory>) {
        launch {
            val db = AppDatabase(getApplication())
            db.budgetCategoryDao().insertAll(*list.toTypedArray())
        }
    }

    // Untuk Expense
    fun addExpense(list: List<Expense>) {
        launch {
            val db = AppDatabase(getApplication())
            db.expenseDao().insertAll(*list.toTypedArray())
        }
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO
}

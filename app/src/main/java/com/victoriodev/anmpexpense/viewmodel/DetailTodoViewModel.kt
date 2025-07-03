package com.victoriodev.anmpexpense.viewmodel

import android.app.Application
import android.util.Log
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

    fun addBudgetCategory(list: List<BudgetCategory>) {
        launch {
            list.forEach {
                Log.d("DEBUG_SAVE", "Saving budget: ${it.nama} (${it.nominal})")
            }
            val db = AppDatabase(getApplication())
            db.budgetCategoryDao().insertAll(*list.toTypedArray())
        }
    }

    fun updateBudgetCategoryFull(uuid: Int, nama: String, nominal: Int, onResult: (Boolean) -> Unit) {
        launch {
            val db = AppDatabase(getApplication())
            val totalExpense = db.expenseDao().getTotalExpenseByBudget(uuid) ?: 0

            if (nominal >= totalExpense) {
                db.budgetCategoryDao().updateBudgetCategoryFull(uuid, nama, nominal)
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun addExpense(list: List<Expense>) {
        launch {
            val db = AppDatabase(getApplication())
            db.expenseDao().insertAll(*list.toTypedArray())
        }
    }


    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO
}

package com.victoriodev.anmpexpense.model

import android.content.Context
import androidx.room.*

/* ========================== ENTITY ========================== */

@Entity(tableName = "user")
data class User(
    @ColumnInfo(name = "username")
    var username: String,
    @ColumnInfo(name = "firstname")
    var firstname: String,
    @ColumnInfo(name = "lastname")
    var lastname: String,
    @ColumnInfo(name = "password")
    var password: String
) {
    @PrimaryKey(autoGenerate = true)
    var userId: Int = 0
}

@Entity(tableName = "budgetCategory")
data class BudgetCategory(
    @ColumnInfo(name = "nameBudget")
    var nama: String,
    @ColumnInfo(name = "nominalBudget")
    var nominal: Int,
    @ColumnInfo(name = "userId")
    var userId: Int
) {
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0
}

@Entity(tableName = "expense")
data class Expense(
    @ColumnInfo(name = "nameExpense")
    var name: String,
    @ColumnInfo(name = "nominalExpense")
    var nominal: Int,
    @ColumnInfo(name = "dateExpense")
    var date: Long,
    @ColumnInfo(name = "budgetCategory")
    var budgetCategoryId: Int,
    @ColumnInfo(name = "userId")
    var userId: Int
) {
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0
}

/* ========================== DAO ========================== */

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg user: User)

    @Query("SELECT * FROM user")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM user WHERE userId = :id LIMIT 1")
    fun getUserByID(id: Int): User
}

@Dao
interface BudgetCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg budgetCategory: BudgetCategory)

    @Query("SELECT * FROM budgetCategory WHERE userId = :userId")
    fun selectAllTodo(userId: Int): List<BudgetCategory>

    @Query("SELECT * FROM budgetCategory WHERE uuid = :uuid AND userId = :userId LIMIT 1")
    fun selectTodo(uuid: Int, userId: Int): BudgetCategory?

    @Query("DELETE FROM budgetCategory WHERE uuid = :uuid AND userId = :userId")
    fun deleteTodo(uuid: Int, userId: Int)

    @Query("UPDATE budgetCategory SET nominalBudget = :newNominal WHERE uuid = :uuid")
    fun updateNominal(uuid: Int, newNominal: Int)

    @Query("UPDATE budgetCategory SET nameBudget = :newNama, nominalBudget = :newNominal WHERE uuid = :uuid")
    fun updateBudgetCategoryFull(uuid: Int, newNama: String, newNominal: Int)

}

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg expense: Expense)

    @Query("SELECT * FROM expense WHERE userId = :userId")
    fun selectAllTodo(userId: Int): List<Expense>

    @Query("SELECT * FROM expense WHERE uuid = :uuid AND userId = :userId LIMIT 1")
    fun selectTodo(uuid: Int, userId: Int): Expense?

    @Query("DELETE FROM expense WHERE uuid = :uuid AND userId = :userId")
    fun deleteTodo(uuid: Int, userId: Int)

    @Query("SELECT * FROM expense WHERE budgetCategory = :categoryId")
    fun selectAllByCategory(categoryId: Int): List<Expense>

    @Query("SELECT SUM(nominalExpense) FROM expense WHERE budgetCategory = :budgetCategoryId")
    fun getTotalExpenseByBudget(budgetCategoryId: Int): Int?

}

/* ========================== DATABASE ========================== */

@Database(
    entities = [User::class, BudgetCategory::class, Expense::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun budgetCategoryDao(): BudgetCategoryDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "anmpexpense.db"
            ).build()
        }

        operator fun invoke(context: Context): AppDatabase {
            return instance ?: synchronized(LOCK) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
        }
    }
}

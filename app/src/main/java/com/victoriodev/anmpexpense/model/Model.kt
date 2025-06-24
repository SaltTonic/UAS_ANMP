package com.victoriodev.anmpexpense.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @ColumnInfo(name="username")
    var username:String,
    @ColumnInfo(name="firstname")
    var firstname:String,
    @ColumnInfo(name="lastname")
    var lastname:String,
    @ColumnInfo(name="password")
    var password:String
) {
    @PrimaryKey(autoGenerate = true)
    var uuid:Int =0
}

data class BudgetCategory(
    @ColumnInfo(name="title")
    var title:String,
    @ColumnInfo(name="notes")
    var notes:String
) {
    @PrimaryKey(autoGenerate = true)
    var uuid:Int =0
}

data class Expense(
    @ColumnInfo(name="title")
    var title:String,
    @ColumnInfo(name="notes")
    var notes:String
) {
    @PrimaryKey(autoGenerate = true)
    var uuid:Int =0
}
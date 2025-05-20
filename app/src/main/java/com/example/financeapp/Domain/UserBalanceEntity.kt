package com.example.financeapp.Domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_balance")
data class UserBalanceEntity(
    @PrimaryKey
    val id: String = "current_balance",
    val balance: Double
) 
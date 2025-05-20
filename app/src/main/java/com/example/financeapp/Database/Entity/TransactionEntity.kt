package com.example.financeapp.Database.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val type: String, // DEPOSIT or WITHDRAW
    val method: String,
    val status: String,
    val date: Date,
    val description: String,
    val isBookmarked: Boolean = false
) 
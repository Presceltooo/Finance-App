package com.example.financeapp.Database.DAO

import androidx.room.*
import com.example.financeapp.Database.Entity.UserBalanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserBalanceDao {
    @Query("SELECT * FROM user_balance WHERE id = :id")
    fun getUserBalance(id: String = "current_balance"): Flow<UserBalanceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserBalance(userBalance: UserBalanceEntity)

    @Update
    suspend fun updateUserBalance(userBalance: UserBalanceEntity)

    @Query("UPDATE user_balance SET balance = :newBalance WHERE id = :id")
    suspend fun updateBalance(newBalance: Double, id: String = "current_balance")
} 
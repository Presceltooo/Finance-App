package com.example.financeapp.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.financeapp.Database.DAO.TransactionDao
import com.example.financeapp.Database.DAO.UserBalanceDao
import com.example.financeapp.Database.Entity.TransactionEntity
import com.example.financeapp.Database.Entity.UserBalanceEntity
import com.example.financeapp.Database.Converters.DateConverter

@Database(
    entities = [TransactionEntity::class, UserBalanceEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun userBalanceDao(): UserBalanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 
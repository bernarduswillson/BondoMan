package com.example.transactionapp.domain.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.transactionapp.domain.db.model.Transaction
import java.util.Date

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactions(): List<Transaction>

    @Query("SELECT * FROM transactions Order By id DESC")
    suspend fun getAllTransactionsDesc(): List<Transaction>

    @Query("SELECT * FROM transactions Order By id ASC")
    suspend fun getAllTransactionsAsc(): List<Transaction>

    @Query("SELECT createdAt FROM transactions")
    suspend fun getAllFormattedDates(): List<Date>
}